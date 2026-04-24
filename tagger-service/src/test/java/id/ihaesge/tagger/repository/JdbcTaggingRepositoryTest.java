package id.ihaesge.tagger.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcTaggingRepositoryTest {

    @Test
    void buildContentCandidatesQueryKeepsTrigramOperatorAndAddsExclusionClause() {
        String sql = JdbcTaggingRepository.buildContentCandidatesQuery(2);

        assertTrue(sql.contains("c.original_content % ta.alias"));
        assertTrue(sql.contains("AND c.id NOT IN (?,?)"));
        assertFalse(sql.contains("{{EXCLUSION_CLAUSE}}"));
    }

    @Test
    void buildContentCandidatesQueryOmitsExclusionClauseWhenNoIds() {
        String sql = JdbcTaggingRepository.buildContentCandidatesQuery(0);

        assertFalse(sql.contains("AND c.id NOT IN"));
        assertFalse(sql.contains("{{EXCLUSION_CLAUSE}}"));
    }
}
