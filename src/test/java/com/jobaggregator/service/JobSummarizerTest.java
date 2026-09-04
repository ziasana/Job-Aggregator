package com.jobaggregator.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JobSummarizerTest {

    @Test
    void summarize_returnsNullForBlankOrNullInput() {
        assertThat(JobSummarizer.summarize(null)).isNull();
        assertThat(JobSummarizer.summarize("   ")).isNull();
    }

    @Test
    void summarize_stripsHtmlTagsAndDecodesCommonEntities() {
        String html = "<p>Join our team &amp; build great things&hellip;</p>";
        assertThat(JobSummarizer.summarize(html)).isEqualTo("Join our team & build great things…");
    }

    @Test
    void summarize_stripsDoublyEscapedHtmlTags() {
        // Observed in practice in Jobicy's jobExcerpt: a literal "&lt;p&gt;"
        // (the entity encoding of a tag), not a real "<p>" tag.
        String doublyEscaped = "&lt;p&gt;Are you a senior developer&lt;/p&gt;";
        assertThat(JobSummarizer.summarize(doublyEscaped)).isEqualTo("Are you a senior developer");
    }

    @Test
    void summarize_collapsesWhitespace() {
        assertThat(JobSummarizer.summarize("Line one\n\n  Line   two")).isEqualTo("Line one Line two");
    }

    @Test
    void summarize_leavesShortTextUnchanged() {
        assertThat(JobSummarizer.summarize("A short description.")).isEqualTo("A short description.");
    }

    @Test
    void summarize_truncatesLongTextAtWordBoundaryWithEllipsis() {
        String longText = "word ".repeat(60).trim();

        String result = JobSummarizer.summarize(longText);

        assertThat(result).endsWith("…");
        assertThat(result.length()).isLessThanOrEqualTo(201);
        assertThat(result).doesNotContain("  ");
    }
}
