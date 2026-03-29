package id.ihaesge.scraper.core;

import java.util.*;

public interface NewsSource {
	String getSourceName();

    List<ArticleItem> getArticleList(int scrapLimit) throws Exception;

    ArticleContent getArticleContent(String url);
}
