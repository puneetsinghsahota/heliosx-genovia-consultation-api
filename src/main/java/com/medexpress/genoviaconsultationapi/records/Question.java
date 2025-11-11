package com.medexpress.genoviaconsultationapi.records;

import java.util.List;

public record Question(
        String id,
        String text,
        String type,
        List<String> options
) {}