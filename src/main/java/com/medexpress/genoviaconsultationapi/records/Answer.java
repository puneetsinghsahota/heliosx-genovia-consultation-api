package com.medexpress.genoviaconsultationapi.records;

/// Used to record submitted answers by the users.
public record Answer(
        String questionId,
        String value
) {}
