package id.ihaesge.scraper.core;

import java.time.LocalDateTime;

public class Content {
    private String originalTitle;
    private LocalDateTime originalPublishDate;
    private String originalContent;
    private String url;
    private String source;

    public Content(String title, LocalDateTime originalPublishDate,
                   String content, String url, String source) {
        this.originalTitle = title;
        this.originalPublishDate = originalPublishDate;
        this.originalContent = content;
        this.url = url;
        this.source = source;
    }

    public Content(String url, String source) {
        this(null, null, null, url, source);
    }

    public Content(String title, String url, String source) {
        this(title, null, null, url, source);
    }

 
    public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	public LocalDateTime getOriginalPublishDate() {
		return originalPublishDate;
	}

	public void setOriginalPublishDate(LocalDateTime originalPublishDate) {
		this.originalPublishDate = originalPublishDate;
	}

	public String getOriginalContent() {
		return originalContent;
	}

	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\noriginalTitle : " + this.originalTitle)
                .append("\noriginalPublishDate : " + this.originalPublishDate)
                .append("\nurl : " + this.url)
                .append("\nsource : " + this.source)
                .append("\noriginalContent : " + this.originalContent);
        return sb.toString();
    }
}
