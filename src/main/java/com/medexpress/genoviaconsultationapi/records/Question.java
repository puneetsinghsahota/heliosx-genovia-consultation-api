package com.medexpress.genoviaconsultationapi.records;

import java.util.List;

// A question which is saved as a record.
public record Question(
        String id, // Distinct ID of the question
        String text, // The actual text of this question
        String type, // Could  be one of radio, text, checkbox etc, can be based on an Enum in further versions
        List<String> options // Options in case of radio button, can be left as an empty list if the type id not radio
) {}