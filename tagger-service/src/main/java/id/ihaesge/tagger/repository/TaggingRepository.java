package id.ihaesge.tagger.repository;

import id.ihaesge.tagger.model.Content;
import id.ihaesge.tagger.model.TagCandidate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TaggingRepository {
    List<Content> findPendingContentIds(String source, Timestamp from, Timestamp to);
    List<TagCandidate> findCandidatesFromOriginalContent(String source, Timestamp from, Timestamp to);
    void saveContentTags(UUID contentId, Set<String> tickers);
    void updateContentStatus(UUID contentId, String statusCode);
    UUID createPipelineLog(String source, String pipeline, Timestamp startAt);
    void updatePipelineLog(UUID pipelineLogId, int totalFound, int totalTagged, int totalUntagged, int totalMultiple, Timestamp endAt);
}
