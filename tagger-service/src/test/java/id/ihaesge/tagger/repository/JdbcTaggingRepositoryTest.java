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

    @Test
    void contentCandidatesQueryRequiresCompanyNameOrMixedCaseContext() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("JOIN stock sk ON sk.ticker = ta.tag"));
        assertTrue(sql.contains("regexp_replace(lower(sk.name), '\\\\mpt\\\\.?\\\\M', ' ', 'g')"));
        assertTrue(sql.contains("'\\\\mtbk\\\\M\\\\.?'"));
        assertTrue(sql.contains("'[^[:alnum:]]+'"));
        assertTrue(sql.contains("c.original_content !~ ('[A-Z\\\\s]{0,50}\\\\m' || ta.tag || '\\\\M[A-Z\\\\s]{0,50}')"));
    }
}
