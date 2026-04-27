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

//        int taggedCount = 0;
//        int untaggedCount = 0;
//        int multipleStocksCount = 0;
        List<Content> pendingContents = new ArrayList<>();
        List<Content> taggedContent = new ArrayList<>();
        List<Content> untaggedContent = new ArrayList<>();
        List<Content> multipleStocksContent = new ArrayList<>();

        try {
            pendingContents = taggingRepository.findPendingContentIds(source, fromTs, toTs);
            System.out.println("===== START TAGGING " + source + " from : " + from.toString() + " To : " + to.toString() + " with size : " + pendingContents.size() + " =====");

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
                    multipleStocksContent.add(content);
                    continue;
                }

                if (distinctTickers.isEmpty()) {
                    taggingRepository.updateContentStatus(contentId, STATUS_UNTAGGED);
                    untaggedContent.add(content);
                    continue;
                }

                String taggedFrom = effectiveCandidates.get(0).taggedFrom();
                taggingRepository.saveContentTags(contentId, distinctTickers, taggedFrom);
                taggingRepository.updateContentStatus(contentId, STATUS_TAGGED);
                taggedContent.add(content);
            }
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
            //printout tagging summary
            System.out.println("\n***** TAGGING SUMMARY *****");

        	System.out.println("TAGGED contents : " + taggedContent.size() + " = " + (taggedContent.size() / pendingContents.size() * 100) + " %");
        	for (Content content : taggedContent) {
        		System.out.println(content.url());
        	}

    		System.out.println("unTAGGED contents : " + untaggedContent.size() + " = " + (untaggedContent.size() / pendingContents.size() * 100) + " %");
        	for (Content content : untaggedContent) {
        		System.out.println(content.url());
        	}

    		System.out.println("multiple TAGS contents : " + multipleStocksContent.size() + " = " + (multipleStocksContent.size() / pendingContents.size() * 100) + " %");
        	for (Content content : multipleStocksContent) {
        		System.out.println(content.url());
        	}

            System.out.println("\n===== STOP TAGGING " + source + " =====");
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
