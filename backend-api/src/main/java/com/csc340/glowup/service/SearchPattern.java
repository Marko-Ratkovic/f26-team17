package com.csc340.glowup.service;

import java.util.Locale;

/** Escapes %, _ and \ so user input matches literally. Queries must add: like :pattern escape '\' */
public final class SearchPattern {

    private SearchPattern() {
    }

    public static String contains(String query) {
        if (query == null || query.isBlank()) {
            return "%%";
        }
        String escaped = query.trim().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
