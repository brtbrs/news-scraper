package id.ihaesge.tagger.engine;

import id.ihaesge.tagger.model.Content;
import id.ihaesge.tagger.model.TagCandidate;
import id.ihaesge.tagger.repository.TaggingRepository;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private static final String PIPELINE_TAGGER = "TAGGER";
    private static final ZoneId ASIA_JAKARTA = ZoneId.of("Asia/Jakarta");

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
        UUID pipelineLogId = null;

        try {
            pipelineLogId = taggingRepository.createPipelineLog(source, PIPELINE_TAGGER, Timestamp.from(nowJakarta()));
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

                System.out.println("===== content url = " + content.url());
                StringBuffer sbtc = new StringBuffer();
                for (TagCandidate tc : effectiveCandidates) {
                	sbtc.append(tc.ticker() + ", ");
                }
                System.out.println("*** effectiveCandidates = " + effectiveCandidates.size() + " = " + sbtc.toString());

                StringBuffer sbdt = new StringBuffer();
                for (String dt : distinctTickers) {
                	sbtc.append(dt + ", ");
                }
                System.out.println("*** distinctTickers = " + distinctTickers.size() + " = " + sbdt.toString());
//                if (distinctTickers.isEmpty() && !effectiveCandidates.isEmpty()) {
//                    Set<String> rawTickers = effectiveCandidates.stream()
//                            .map(TagCandidate::ticker)
//                            .collect(Collectors.toCollection(LinkedHashSet::new));
//                    System.out.println("[TAGGER][EMPTY_TICKERS] contentId=" + contentId
//                            + " candidates=" + effectiveCandidates.size()
//                            + " rawTickers=" + rawTickers
//                            + " url=" + content.url());
//                }

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

                taggingRepository.saveContentTags(contentId, distinctTickers);
                taggingRepository.updateContentStatus(contentId, STATUS_TAGGED);
                taggedContent.add(content);
            }
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
            if (pipelineLogId != null) {
                taggingRepository.updatePipelineLog(
                        pipelineLogId,
                        taggedContent.size(),
                        untaggedContent.size(),
                        multipleStocksContent.size(),
                        Timestamp.from(nowJakarta())
                );
            }

            //printout tagging summary
            System.out.println("\n***** TAGGING SUMMARY *****");

            DecimalFormat df = new DecimalFormat("0.00");
            int totalPending = pendingContents.size();
            double percentage = totalPending == 0 ? 0 : (double) taggedContent.size() / totalPending * 100;
        	System.out.println("=== TAGGED contents : " + taggedContent.size() + " = " + df.format(percentage) + "%");
        	for (Content content : taggedContent) {
        		System.out.println(content.url());
        	}

        	percentage = totalPending == 0 ? 0 : (double) untaggedContent.size() / totalPending * 100;
    		System.out.println("=== unTAGGED contents : " + untaggedContent.size() + " = " + df.format(percentage) + "%");
        	for (Content content : untaggedContent) {
        		System.out.println(content.url());
        	}

        	percentage = totalPending == 0 ? 0 : (double) multipleStocksContent.size() / totalPending * 100;
    		System.out.println("=== multiple TAGS contents : " + multipleStocksContent.size() + " = " + df.format(percentage) + "%");
        	for (Content content : multipleStocksContent) {
        		System.out.println(content.url());
        	}

            System.out.println("\n***** STOP TAGGING " + source + " *****");
        }
    }

    private Map<UUID, List<TagCandidate>> toCandidatesByContent(List<TagCandidate> candidates) {
        Map<UUID, List<TagCandidate>> candidatesByContent = new LinkedHashMap<>();
        for (TagCandidate candidate : candidates) {
            candidatesByContent.computeIfAbsent(candidate.contentId(), key -> new ArrayList<>()).add(candidate);
        }
        return candidatesByContent;
    }

    private Instant nowJakarta() {
        return ZonedDateTime.now(ASIA_JAKARTA).toInstant();
    }
}
