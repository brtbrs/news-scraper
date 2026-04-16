package id.ihaesge.tagger.engine;

import id.ihaesge.tagger.api.ApiContentClient;
import id.ihaesge.tagger.api.ApiTaggingClient;
import id.ihaesge.tagger.model.ContentItem;
import id.ihaesge.tagger.model.TagAliasItem;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentTaggerEngine {
    private static final String STATUS_TAGGED = "TAGGED";
    private static final String STATUS_UNTAGGED = "UNTAGGED";
    private static final String STATUS_MULTIPLE_STOCKS = "MULTIPLE_STOCKS";
    private static final Pattern STOCK_TICKER_PATTERN = Pattern.compile("(?i)\\b([a-z]{4})(?:\\.jk)?\\b");

    private final ApiContentClient apiContentClient;
    private final ApiTaggingClient apiTaggingClient;

    public ContentTaggerEngine(String apiBaseUrl) {
        this.apiContentClient = new ApiContentClient(apiBaseUrl);
        this.apiTaggingClient = new ApiTaggingClient(apiBaseUrl);
    }

    public void run(String source, Instant from, Instant to) {
        List<TagAliasItem> aliases = apiTaggingClient.getTagAliases();
        List<ContentItem> contents = apiContentClient.getContentsBySourceAndDateRange(source, from, to);

        System.out.println("Tagging source=" + source + ", from=" + from + ", to=" + to + ", contents=" + contents.size());
        for (ContentItem content : contents) {
            processSingleContent(content, aliases);
        }
    }

    private void processSingleContent(ContentItem content, List<TagAliasItem> aliases) {
        String fullText = ((content.originalTitle() == null ? "" : content.originalTitle()) + " "
                + (content.originalContent() == null ? "" : content.originalContent())).trim();

        Set<String> foundTickers = findDistinctTickers(fullText);
        if (foundTickers.size() > 5) {
            apiContentClient.updateContentStatus(content.id(), STATUS_MULTIPLE_STOCKS);
            System.out.println("MULTIPLE_STOCKS contentId=" + content.id() + " tickers=" + foundTickers.size());
            return;
        }

        Set<String> matchedTags = new LinkedHashSet<>();
        for (TagAliasItem aliasItem : aliases) {
            if (aliasItem.alias() == null || aliasItem.alias().isBlank() || aliasItem.tag() == null || aliasItem.tag().isBlank()) {
                continue;
            }

            Pattern keywordPattern = Pattern.compile("(?i)" + Pattern.quote(aliasItem.alias()));
            Matcher matcher = keywordPattern.matcher(fullText);
            if (matcher.find()) {
                matchedTags.add(aliasItem.tag());
            }
        }

        if (matchedTags.isEmpty()) {
            apiContentClient.updateContentStatus(content.id(), STATUS_UNTAGGED);
            System.out.println("UNTAGGED contentId=" + content.id());
            return;
        }

        for (String tag : matchedTags) {
            apiTaggingClient.createContentTag(content.id(), tag);
        }

        apiContentClient.updateContentStatus(content.id(), STATUS_TAGGED);
        System.out.println("TAGGED contentId=" + content.id() + " tags=" + matchedTags);
    }

    private Set<String> findDistinctTickers(String fullText) {
        Set<String> tickers = new LinkedHashSet<>();
        Matcher matcher = STOCK_TICKER_PATTERN.matcher(fullText);
        while (matcher.find()) {
            tickers.add(matcher.group(1).toUpperCase());
        }
        return tickers;
    }
}
