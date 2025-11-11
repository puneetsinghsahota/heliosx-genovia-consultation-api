package com.medexpress.genoviaconsultationapi.service;

import com.medexpress.genoviaconsultationapi.records.Answer;
import com.medexpress.genoviaconsultationapi.records.EligibilityResponse;
import com.medexpress.genoviaconsultationapi.records.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationController.class);
    private final ConsultationService service;

    public ConsultationController(ConsultationService service) {
        this.service = service;
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions() {
        logger.info("GET /api/consultation/questions called.");
        return ResponseEntity.ok(service.getQuestions());
    }

    @PostMapping("/answers")
    public ResponseEntity<EligibilityResponse> submitAnswers(@RequestBody List<Answer> answers) {
        logger.info("POST /api/consultation/answers called");
        if (answers == null || answers.isEmpty()) {
            logger.error("POST /api/consultation/answers called with empty answers.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answers list cannot be empty.");
        }
        try {
            EligibilityResponse response = service.processAnswers(answers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("POST /api/consultation/answers caused internal server error.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }
}
