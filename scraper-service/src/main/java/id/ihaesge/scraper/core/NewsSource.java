package id.ihaesge.scraper.core;

import java.util.*;

public interface NewsSource {
	String getSourceName();

    List<Content> getArticleList(int scrapLimit) throws Exception;

    Content getContent(String url);
}
