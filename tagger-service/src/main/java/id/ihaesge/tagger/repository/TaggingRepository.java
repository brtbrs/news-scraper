package id.ihaesge.tagger.repository;

import id.ihaesge.tagger.model.TagCandidate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TaggingRepository {
    List<UUID> findPendingContentIds(String source, Timestamp from, Timestamp to);
    List<TagCandidate> findCandidatesFromOriginalContent(String source, Timestamp from, Timestamp to);
    void saveContentTags(UUID contentId, Set<String> tickers, String taggedFrom);
    void updateContentStatus(UUID contentId, String statusCode);
}
