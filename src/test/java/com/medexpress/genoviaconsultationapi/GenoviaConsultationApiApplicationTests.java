// src/test/java/com/medexpress/genoviaconsultationapi/GenoviaConsultationApiApplicationTests.java
package com.medexpress.genoviaconsultationapi;

import com.medexpress.genoviaconsultationapi.records.Answer;
import com.medexpress.genoviaconsultationapi.records.EligibilityResponse;
import com.medexpress.genoviaconsultationapi.records.Question;
import com.medexpress.genoviaconsultationapi.service.ConsultationController;
import com.medexpress.genoviaconsultationapi.service.ConsultationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenoviaConsultationApiApplicationTests {

    private final ConsultationService service = new ConsultationService();

    @Test
    void getQuestions_returnsAllQuestions() {
        List<Question> questions = service.getQuestions();
        assertEquals(4, questions.size());
        assertTrue(questions.stream().anyMatch(q -> q.id().equals("Q1")));
    }

    @Test
    void processAnswers_incompleteAnswers_returnsNotEligible() {
        List<Answer> answers = List.of(
                new Answer("Q1", "No")
        );
        EligibilityResponse response = service.processAnswers(answers);
        assertFalse(response.eligible());
        assertFalse(response.doctorNotified());
        assertTrue(response.message().contains("Incomplete Answers"));
    }

    @Test
    void processAnswers_previousReactionYes_returnsNotEligible() {
        List<Answer> answers = List.of(
                new Answer("Q1", "Yes"),
                new Answer("Q2", "3"),
                new Answer("Q3", "Yes"),
                new Answer("Q4", "")
        );
        EligibilityResponse response = service.processAnswers(answers);
        assertFalse(response.eligible());
        assertTrue(response.doctorNotified());
        assertTrue(response.message().contains("previous adverse reaction"));
    }

    @Test
    void processAnswers_ageNotConfirmed_returnsNotEligible() {
        List<Answer> answers = List.of(
                new Answer("Q1", "No"),
                new Answer("Q2", "3"),
                new Answer("Q3", "No"),
                new Answer("Q4", "")
        );
        EligibilityResponse response = service.processAnswers(answers);
        assertFalse(response.eligible());
        assertTrue(response.doctorNotified());
        assertTrue(response.message().contains("Underage"));
    }

    @Test
    void processAnswers_mildSeverity_returnsEligibleWithWarning() {
        List<Answer> answers = List.of(
                new Answer("Q1", "No"),
                new Answer("Q2", "2"),
                new Answer("Q3", "Yes"),
                new Answer("Q4", "med 1, med 2")
        );
        EligibilityResponse response = service.processAnswers(answers);
        assertTrue(response.eligible());
        assertTrue(response.doctorNotified());
        assertTrue(response.message().contains("OTC medications"));
    }

    @Test
    void processAnswers_allChecksPassed_returnsEligible() {
        List<Answer> answers = List.of(
                new Answer("Q1", "No"),
                new Answer("Q2", "4"),
                new Answer("Q3", "Yes"),
                new Answer("Q4", "None")
        );
        EligibilityResponse response = service.processAnswers(answers);
        assertTrue(response.eligible());
        assertTrue(response.doctorNotified());
        assertTrue(response.message().contains("Prescription Under Review"));
    }

    @Test
    void processAnswers_severityNotANumber_throwsInternalServerError() {
        ConsultationController controller = new ConsultationController(service);
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            controller.submitAnswers(List.of(
                    new Answer("Q1", "No"),
                    new Answer("Q2", "not_a_number"),
                    new Answer("Q3", "Yes"),
                    new Answer("Q4", "")
            ));
        });
        assertEquals(500, thrown.getStatusCode().value());
    }

    @Test
    void controller_submitAnswers_emptyList_throwsBadRequest() {
        ConsultationController controller = new ConsultationController(service);
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            controller.submitAnswers(List.of());
        });
        assertEquals(400, thrown.getStatusCode().value());
    }

    @Test
    void controller_getQuestions_returnsQuestions() {
        ConsultationController controller = new ConsultationController(service);
        ResponseEntity<List<Question>> response = controller.getQuestions();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(4, response.getBody().size());
    }

    @Test
    void controller_submitAnswers_valid_returnsEligibilityResponse() {
        ConsultationController controller = new ConsultationController(service);
        List<Answer> answers = List.of(
                new Answer("Q1", "No"),
                new Answer("Q2", "3"),
                new Answer("Q3", "Yes"),
                new Answer("Q4", "")
        );
        ResponseEntity<EligibilityResponse> response = controller.submitAnswers(answers);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().eligible());
    }
}
