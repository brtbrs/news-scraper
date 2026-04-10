package id.ihaesge.scraper.engine;

import java.util.*;
import id.ihaesge.scraper.core.*;

public class NewsScraperEngine {
	private final List<NewsSource> sources = new ArrayList<>();

    public void registerSource(NewsSource source) {
        sources.add(source);
    }

    public List<ArticleContent> scrapeAll(int scrapLimit) {
        List<ArticleContent> results = new ArrayList<>();

        for (NewsSource source : sources) {
            try {
                List<ArticleItem> items = source.getArticleList(scrapLimit);

                for (ArticleItem item : items) {
                    ArticleContent content = source.getArticleContent(item.url);

                    if (content != null) {
                        results.add(content);
                        System.out.println("\n***** content *****" + content.toString());
                    }
                }

            } catch (Exception e) {
                System.out.println("Error in source: " + source.getSourceName());
            	e.printStackTrace();
            }
        }

        return results;
    }
}
