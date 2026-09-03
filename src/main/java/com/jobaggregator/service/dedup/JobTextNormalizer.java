package com.jobaggregator.service.dedup;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Normalizes free-text job fields (title/company/location) for comparison:
 * strips diacritics, lowercases, drops punctuation, collapses whitespace.
 */
final class JobTextNormalizer {

    private JobTextNormalizer() {
    }

    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String withoutDiacritics = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutDiacritics
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }
}
