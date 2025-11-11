package com.medexpress.genoviaconsultationapi.records;

/// Used to respond to the FrontEnd with the response around the patient's eligibility for the medication being offered
public record EligibilityResponse(
        boolean eligible, // Whether a user is eligible
        boolean doctorNotified, // Whether a doctor has been notified or not
        String message // A corresponding message to show to the user
) {}