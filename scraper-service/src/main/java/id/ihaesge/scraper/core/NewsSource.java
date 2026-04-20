package id.ihaesge.scraper.core;

import java.util.*;

public interface NewsSource {
	public final String WEBSITE = "WEBSITE";
	public final String SITEMAP = "SITEMAP";

	String getSourceName();

    List<String> getNewsList(int scrapLimit, String from) throws Exception;

    Content getNewsDetail(String url) throws Exception;
}
