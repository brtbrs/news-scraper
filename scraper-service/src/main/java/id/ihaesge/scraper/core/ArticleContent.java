package id.ihaesge.scraper.core;

import java.time.LocalDateTime;

public class ArticleContent {
	public String title;
    public LocalDateTime publishDate;
    public String content;
    public String url;
    public String source;

    public ArticleContent(String title, LocalDateTime publishDate,
                          String content, String url, String source) {
        this.title = title;
        this.publishDate = publishDate;
        this.content = content;
        this.url = url;
        this.source = source;
    }

    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("\ntitle : " + this.title).append("\npublishDate : " + this.publishDate).append("\nurl : " + this.url).append("\nsource : " + this.source).append("\ncontent : " + this.content);
    	return sb.toString();
    }
}
