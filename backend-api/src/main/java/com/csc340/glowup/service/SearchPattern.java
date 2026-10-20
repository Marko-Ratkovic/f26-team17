package com.csc340.glowup.service;

import java.util.Locale;

/**
 * Builds a safe "contains" pattern for SQL LIKE searches.
 * The characters %, _ and \ typed by a user are escaped, so searching "a_b"
 * matches the text "a_b" instead of "a" + any character + "b".
 * Queries using it must add:  like :pattern escape '\'
 */
public final class SearchPattern {

    private SearchPattern() {
    }

    /** "Laura" → "%laura%"; null or blank → "%%" (matches everything). */
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
