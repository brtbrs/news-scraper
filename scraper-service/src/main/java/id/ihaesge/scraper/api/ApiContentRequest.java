package id.ihaesge.scraper.api;

public record ApiContentRequest(
        String source,
        String originalTitle,
        String originalContent,
        String url,
        String originalLanguage,
        String originalPublishDate,
        String publishDate
) {
}
