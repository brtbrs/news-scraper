package id.ihaesge.scraper.core;

import java.time.LocalDateTime;

public class Content {
    public String title;
    public LocalDateTime originalPublishDate;
    public String content;
    public String url;
    public String source;

    public Content(String title, LocalDateTime originalPublishDate,
                   String content, String url, String source) {
        this.title = title;
        this.originalPublishDate = originalPublishDate;
        this.content = content;
        this.url = url;
        this.source = source;
    }

    public Content(String title, String url, String source) {
        this(title, null, null, url, source);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\ntitle : " + this.title)
                .append("\noriginalPublishDate : " + this.originalPublishDate)
                .append("\nurl : " + this.url)
                .append("\nsource : " + this.source)
                .append("\ncontent : " + this.content);
        return sb.toString();
    }
}
