package id.ihaesge.tagger.repository;

import id.ihaesge.tagger.model.Content;
import id.ihaesge.tagger.model.TagCandidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JdbcTaggingRepository implements TaggingRepository {
    private static final Set<String> GENERIC_ALIASES = Set.of("bank");

    private static final String BASE_FILTER = """
            FROM content c
            JOIN source s ON s.id = c.source
            JOIN attribute st ON st.id = c.status
            WHERE s.name = ?
              AND c.original_publish_date >= ?
              AND c.original_publish_date <= ?
              AND st.type = 'CONTENT_STATUS'
              AND st.code = 'PENDING'
            """;

    private static final String QUERY_PENDING_CONTENT = """
            SELECT c.id,
                   c.original_title,
                   c.original_publish_date,
                   c.original_content,
                   c.url,
                   s.name AS source
            """ + BASE_FILTER;

    static final String QUERY_CONTENT_CANDIDATES = """
            SELECT DISTINCT c.id AS content_id, ta.tag, ta.alias, 'ORIGINAL_CONTENT' AS tagged_from
            FROM content c
            JOIN source s ON s.id = c.source
            JOIN attribute st ON st.id = c.status
            JOIN tag_alias ta ON (
                (
                    LENGTH(ta.alias) > 4
                    AND ta.alias !~ '\\s+'
                    AND c.original_content % ta.alias
                    AND c.original_content ILIKE '%' || ta.alias || '%'
                )
                OR
                (
                    LENGTH(ta.alias) > 4
                    AND ta.alias ~ '\\s+'
                    AND c.original_content ~* ('\\m' || regexp_replace(ta.alias, '\\s+', '\\\\s+', 'g') || '\\M')
                )
                OR
                (
                    LENGTH(ta.alias) <= 4
                    AND (
                        (
                            ta.alias = upper(ta.alias)
                            AND c.original_content ~ ('\\m' || regexp_replace(ta.alias, '\\s+', '\\\\s+', 'g') || '\\M')
                        )
                        OR
                        (
                            ta.alias <> upper(ta.alias)
                            AND c.original_content ~* ('\\m' || regexp_replace(ta.alias, '\\s+', '\\\\s+', 'g') || '\\M')
                        )
                    )
                )
            )
            WHERE s.name = ?
              AND c.original_publish_date >= ?
              AND c.original_publish_date <= ?
              AND st.type = 'CONTENT_STATUS'
              AND st.code = 'PENDING'
              AND LENGTH(trim(ta.alias)) > 1
              AND lower(trim(ta.alias)) NOT IN ('bank')
            """;

    private static final String INSERT_CONTENT_TAG = """
            INSERT INTO content_tag (content, tag, tagged_from)
            VALUES (?, ?, ?)
            ON CONFLICT (content, tag) DO NOTHING
            """;

    private static final String UPDATE_CONTENT_STATUS = """
            UPDATE content c
            SET status = a.id
            FROM attribute a
            WHERE c.id = ?
              AND a.type = 'CONTENT_STATUS'
              AND a.code = ?
            """;

    private final Connection connection;

    public JdbcTaggingRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Content> findPendingContentIds(String source, Timestamp from, Timestamp to) {
        try (PreparedStatement stmt = connection.prepareStatement(QUERY_PENDING_CONTENT)) {
            bindBaseFilter(stmt, source, from, to);
            List<Content> contents = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contents.add(new Content(
                            rs.getObject("id", UUID.class),
                            rs.getString("original_title"),
                            rs.getTimestamp("original_publish_date").toInstant(),
                            rs.getString("original_content"),
                            rs.getString("url"),
                            rs.getString("source")
                    ));
                }
            }
            return contents;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load pending content", e);
        }
    }

    @Override
    public List<TagCandidate> findCandidatesFromOriginalContent(String source, Timestamp from, Timestamp to) {
        try (PreparedStatement stmt = connection.prepareStatement(QUERY_CONTENT_CANDIDATES)) {
            bindBaseFilter(stmt, source, from, to);
            return mapCandidates(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query content tag candidates", e);
        }
    }

    @Override
    public void saveContentTags(UUID contentId, Set<String> tickers, String taggedFrom) {
        Set<String> uniqueTickers = tickers.stream().filter(t -> t != null && !t.isBlank()).collect(Collectors.toCollection(HashSet::new));
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_CONTENT_TAG)) {
            for (String ticker : uniqueTickers) {
                stmt.setObject(1, contentId);
                stmt.setString(2, ticker);
                stmt.setString(3, taggedFrom);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save content tags", e);
        }
    }

    @Override
    public void updateContentStatus(UUID contentId, String statusCode) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_CONTENT_STATUS)) {
            stmt.setObject(1, contentId);
            stmt.setString(2, statusCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update content status", e);
        }
    }

    private List<TagCandidate> mapCandidates(PreparedStatement stmt) throws SQLException {
        List<TagCandidate> candidates = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String alias = rs.getString("alias");
                if (alias == null || alias.isBlank() || GENERIC_ALIASES.contains(alias.trim().toLowerCase())) {
                    continue;
                }
                candidates.add(new TagCandidate(
                        rs.getObject("content_id", UUID.class),
                        rs.getString("tag"),
                        alias,
                        rs.getString("tagged_from")
                ));
            }
        }
        return candidates;
    }

    private int bindBaseFilter(PreparedStatement stmt, String source, Timestamp from, Timestamp to) throws SQLException {
        stmt.setString(1, source);
        stmt.setTimestamp(2, from);
        stmt.setTimestamp(3, to);
        return 4;
    }

}
