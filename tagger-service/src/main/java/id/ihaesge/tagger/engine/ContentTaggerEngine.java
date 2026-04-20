package id.ihaesge.tagger.engine;

import id.ihaesge.tagger.model.TagCandidate;
import id.ihaesge.tagger.repository.TaggingRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContentTaggerEngine {
    private static final String STATUS_TAGGED = "TAGGED";
    private static final String STATUS_UNTAGGED = "UNTAGGED";
    private static final String STATUS_MULTIPLE_STOCKS = "MULTIPLE_STOCKS";

    private final TaggingRepository taggingRepository;

    public ContentTaggerEngine(TaggingRepository taggingRepository) {
        this.taggingRepository = taggingRepository;
    }

    public void run(String source, Instant from, Instant to) {
        Timestamp fromTs = Timestamp.from(from);
        Timestamp toTs = Timestamp.from(to);

        List<UUID> pendingContentIds = taggingRepository.findPendingContentIds(source, fromTs, toTs);
        Map<UUID, List<TagCandidate>> titleCandidatesByContent = toCandidatesByContent(
                taggingRepository.findCandidatesFromOriginalTitle(source, fromTs, toTs)
        );

        Set<UUID> titleMatchedContentIds = titleCandidatesByContent.keySet();
        Map<UUID, List<TagCandidate>> contentCandidatesByContent = toCandidatesByContent(
                taggingRepository.findCandidatesFromOriginalContent(source, fromTs, toTs, titleMatchedContentIds)
        );

        int taggedCount = 0;
        int untaggedCount = 0;
        int multipleStocksCount = 0;

        for (UUID contentId : pendingContentIds) {
            List<TagCandidate> effectiveCandidates = titleCandidatesByContent.get(contentId);
            if (effectiveCandidates == null || effectiveCandidates.isEmpty()) {
                effectiveCandidates = contentCandidatesByContent.getOrDefault(contentId, List.of());
            }

            Set<String> distinctTickers = effectiveCandidates.stream()
                    .map(TagCandidate::ticker)
                    .filter(ticker -> ticker != null && !ticker.isBlank())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            if (distinctTickers.size() > 5) {
                taggingRepository.updateContentStatus(contentId, STATUS_MULTIPLE_STOCKS);
                multipleStocksCount++;
                continue;
            }

            if (distinctTickers.isEmpty()) {
                taggingRepository.updateContentStatus(contentId, STATUS_UNTAGGED);
                untaggedCount++;
                continue;
            }

            String taggedFrom = effectiveCandidates.get(0).taggedFrom();
            taggingRepository.saveContentTags(contentId, distinctTickers, taggedFrom);
            taggingRepository.updateContentStatus(contentId, STATUS_TAGGED);
            taggedCount++;
        }

        System.out.println("Tagging completed source=" + source
                + " pending=" + pendingContentIds.size()
                + " tagged=" + taggedCount
                + " untagged=" + untaggedCount
                + " multipleStocks=" + multipleStocksCount);
    }

    private Map<UUID, List<TagCandidate>> toCandidatesByContent(List<TagCandidate> candidates) {
        Map<UUID, List<TagCandidate>> candidatesByContent = new LinkedHashMap<>();
        for (TagCandidate candidate : candidates) {
            candidatesByContent.computeIfAbsent(candidate.contentId(), key -> new ArrayList<>()).add(candidate);
        }
        return candidatesByContent;
    }
}
