package id.ihaesge.scraper.api;

public record ApiArticleRequest(
        String source,
        String title,
        String content,
        String url,
        String publishedAt
) {
}
