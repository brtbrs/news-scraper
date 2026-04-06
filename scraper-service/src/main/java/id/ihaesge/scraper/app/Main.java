package id.ihaesge.scraper.app;

import java.util.List;

import id.ihaesge.scraper.api.ApiArticleClient;
import id.ihaesge.scraper.core.ArticleContent;
import id.ihaesge.scraper.engine.NewsScraperEngine;
import id.ihaesge.scraper.sources.*;

public class Main {
    public static void main(String[] args) {
        int scrapLimit = readScrapeLimit(args);
        System.out.println("=== scrape run started with limit : " + scrapLimit + " ===");
        String apiBaseUrl = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080/api");

        NewsScraperEngine engine = new NewsScraperEngine();
        registerSources(engine);

        List<ArticleContent> results = engine.scrapeAll(scrapLimit);
        ApiArticleClient apiArticleClient = new ApiArticleClient(apiBaseUrl);

//        System.out.println("Scraped " + results.size() + " articles. Sending to API: " + apiBaseUrl);
//        for (ArticleContent article : results) {
//            apiArticleClient.sendArticle(article);
//        }

        System.out.println("=== scrape run finished ===");
    }

    private static void registerSources(NewsScraperEngine engine) {
//		engine.registerSource(new Bisnis());
//		engine.registerSource(new CNBC());
		engine.registerSource(new EmitenNews());
//		engine.registerSource(new EmitenTrust());
//		engine.registerSource(new Investor());
//		engine.registerSource(new IPOTNews());
//		engine.registerSource(new KabarBursa());
//		engine.registerSource(new KataData());
//		engine.registerSource(new Kontan());
//		engine.registerSource(new Neraca());
//		engine.registerSource(new StockWatch());
    }

    private static int readScrapeLimit(String[] args) {
        int defaultLimit = 2;
        for (String arg : args) {
            if (arg.startsWith("--limit=")) {
                try {
                    return Integer.parseInt(arg.substring("--limit=".length()));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid --limit value. Falling back to default=" + defaultLimit);
                }
            }
        }
        return defaultLimit;
    }
}
