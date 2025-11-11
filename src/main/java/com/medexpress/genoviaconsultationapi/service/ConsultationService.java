package com.medexpress.genoviaconsultationapi.service;

import com.medexpress.genoviaconsultationapi.records.Answer;
import com.medexpress.genoviaconsultationapi.records.EligibilityResponse;
import com.medexpress.genoviaconsultationapi.records.Question;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

///  Main business logic for the Consultation API.
@Service
public class ConsultationService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationService.class);
    private final Map<String, Question> staticQuestions;

    public ConsultationService() {
        List<Question> questions = List.of(
                new Question("Q1", "Have you ever had an adverse reaction to similar medication?", "radio", List.of("Yes", "No")),
                new Question("Q2", "On a scale of 1 to 5, map adversity of Allergy?", "radio", List.of("1", "2", "3", "4", "5")),
                new Question("Q3", "over 18?", "radio", List.of("Yes", "No")),
                new Question("Q4", "Any other current prescription?", "text", List.of())
        );
        this.staticQuestions = questions.stream()
                .collect(Collectors.toMap(Question::id, q -> q));
    }

    public List<Question> getQuestions() {
        return staticQuestions.values().stream().toList();
    }

    public EligibilityResponse processAnswers(List<Answer> answers) {
        if (answers.size() < staticQuestions.size()) {
            logger.warn("There are less than required answers for the question");
            return new EligibilityResponse(false, false, "Incomplete Answers. Please ensure all questions are answered.");
        }

        Map<String, String> answersMap = answers.stream()
                .collect(Collectors.toMap(Answer::questionId, Answer::value));

        String reactionAnswer = answersMap.get("Q1");
        if (reactionAnswer != null && reactionAnswer.equalsIgnoreCase("Yes")) {
            logger.warn("Answer for Adverse Reaction is Yes");
            return new EligibilityResponse(false, true, "Unable to prescribe medication due to previous adverse reaction, A doctor will contact you.");
        }

        String ageAnswer = answersMap.get("Q3");
        if (ageAnswer != null && !ageAnswer.equalsIgnoreCase("Yes")) {
            logger.warn("Under Age User");
            return new EligibilityResponse(false, true, "Underage, a doctor will contact you");
        }

        String severityAnswer = answersMap.get("Q2");
        if (severityAnswer != null) {
                int severity = Integer.parseInt(severityAnswer);
                if (severity <= 2) {
                    logger.warn("Under Severity User");
                    return new EligibilityResponse(true, true, "Prescribed and Under Review but OTC medications might be needed.");
                }
        }
        logger.warn("Medication Under Review with Doctor");
        return new EligibilityResponse(true, true, "Prescription Under Review");
    }
}
