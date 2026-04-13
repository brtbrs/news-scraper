package id.ihaesge.scraper.core;

import java.util.*;

public interface NewsSource {
	String getSourceName();

    List<Content> getNewsList(int scrapLimit, boolean fromSiteMap) throws Exception;

    Content getNewsDetail(String url) throws Exception;
}
