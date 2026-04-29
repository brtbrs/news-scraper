package id.ihaesge.tagger.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcTaggingRepositoryTest {

    @Test
    void contentCandidatesQueryUsesWholeWordTickerMatching() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("c.original_content ~ ('\\\\m' || sk.ticker || '\\\\M')"));
    }

    @Test
    void contentCandidatesQueryAllowsCompanyNameMatchCaseInsensitively() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("c.original_content ILIKE ('%' || sk.pure_name || '%')"));
    }

    @Test
    void contentCandidatesQueryRejectsTickerInsideLongUppercaseSentences() {
        String sql = JdbcTaggingRepository.QUERY_CONTENT_CANDIDATES;

        assertTrue(sql.contains("\\\\m[A-Z]{2,}(\\\\s+[A-Z]{2,}){2,}\\\\s+"));
        assertTrue(sql.contains("\\\\s+[A-Z]{2,}(\\\\s+[A-Z]{2,}){2,}\\\\M"));
    }
}
