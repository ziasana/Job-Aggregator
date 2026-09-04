package com.jobaggregator.service;

/**
 * Turns a raw third-party job description (plain text, real HTML, or -
 * observed in practice from Jobicy's excerpt field - HTML that's itself
 * been entity-escaped, e.g. a literal "&lt;p&gt;") into a short, plain-text
 * summary safe to display directly. Never render the raw description as
 * HTML: it's third-party content (XSS risk via dangerouslySetInnerHTML-style
 * rendering on the frontend).
 */
final class JobSummarizer {

    private static final int MAX_LENGTH = 200;

    private JobSummarizer() {
    }

    static String summarize(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        // Strip real tags, decode entities (which may reveal escaped tags
        // like a literal "&lt;p&gt;"), then strip tags again.
        String text = stripTags(description);
        text = decodeEntities(text);
        text = stripTags(text);
        text = text.replaceAll("\\s+", " ").trim();

        if (text.isEmpty()) {
            return null;
        }
        if (text.length() <= MAX_LENGTH) {
            return text;
        }

        int cutoff = text.lastIndexOf(' ', MAX_LENGTH);
        String truncated = cutoff > 0 ? text.substring(0, cutoff) : text.substring(0, MAX_LENGTH);
        return truncated.trim() + "…";
    }

    private static String stripTags(String text) {
        return text.replaceAll("<[^>]+>", " ");
    }

    private static String decodeEntities(String text) {
        return text
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace("&hellip;", "…")
                .replace("&nbsp;", " ")
                .replace("&mdash;", "—")
                .replace("&ndash;", "–")
                .replace("&rsquo;", "’")
                .replace("&lsquo;", "‘")
                .replace("&amp;", "&");
    }
}
