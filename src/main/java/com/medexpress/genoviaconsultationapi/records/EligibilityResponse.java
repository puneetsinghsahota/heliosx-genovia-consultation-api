package com.medexpress.genoviaconsultationapi.records;

public record EligibilityResponse(
        boolean eligible,
        boolean doctorNotified,
        String message
) {}