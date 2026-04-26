package id.ihaesge.tagger.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcTaggingRepositoryTest {

    @Test
    void contentCandidatesQueryKeepsTrigramOperator() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("c.original_content % ta.alias"));
    }

    @Test
    void contentCandidatesQueryUsesCaseSensitiveMatchingForShortUppercaseAliases() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("ta.alias = upper(ta.alias)"));
        assertTrue(sql.contains("c.original_content ~ ('\\\\m' || regexp_replace(ta.alias, '\\\\s+', '\\\\\\\\s+', 'g') || '\\\\M')"));
    }
}
