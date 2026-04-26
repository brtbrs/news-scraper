package id.ihaesge.tagger.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcTaggingRepositoryTest {

    @Test
    void contentCandidatesQueryMatchesOnlyUppercaseFourLetterTags() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("ta.tag ~ '^[A-Z]{4}$'"));
    }

    @Test
    void contentCandidatesQueryUsesCaseSensitiveWholeWordTickerMatching() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("c.original_content ~ ('\\\\m' || ta.tag || '\\\\M')"));
    }
}
