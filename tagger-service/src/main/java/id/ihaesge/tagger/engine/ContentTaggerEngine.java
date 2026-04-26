package id.ihaesge.tagger.engine;

import id.ihaesge.tagger.model.Content;
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

        int taggedCount = 0;
        int untaggedCount = 0;
        int multipleStocksCount = 0;
        List<Content> pendingContents = new ArrayList<>();

        try {
            pendingContents = taggingRepository.findPendingContentIds(source, fromTs, toTs);
            Map<UUID, List<TagCandidate>> contentCandidatesByContent = toCandidatesByContent(
                    taggingRepository.findCandidatesFromOriginalContent(source, fromTs, toTs)
            );

            for (Content content : pendingContents) {
                UUID contentId = content.id();
                List<TagCandidate> effectiveCandidates = contentCandidatesByContent.getOrDefault(contentId, List.of());

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
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
            //printout tagging summary
            System.out.println("\n***** TAGGING SUMMARY *****");
            System.out.println("TOTAL FOUND : " + pendingContents.size());

    		System.out.println("unTAGGED contents : " + untaggedCount);
    		System.out.println("multiple tag contents : " + multipleStocksCount);
//        	for (String unprocessedURL : skipped) {
//        		System.out.println(unprocessedURL);
//        	}

        	System.out.println("TAGGED contents : " + taggedCount);
//        	for (Content savedContent : saved) {
//        		System.out.println(savedContent.getUrl());
//        	}

//            System.out.println("\n===== STOP SCRAPING " + source.getSourceName() + " =====");
        }
    }

    private Map<UUID, List<TagCandidate>> toCandidatesByContent(List<TagCandidate> candidates) {
        Map<UUID, List<TagCandidate>> candidatesByContent = new LinkedHashMap<>();
        for (TagCandidate candidate : candidates) {
            candidatesByContent.computeIfAbsent(candidate.contentId(), key -> new ArrayList<>()).add(candidate);
        }
        return candidatesByContent;
    }
}
