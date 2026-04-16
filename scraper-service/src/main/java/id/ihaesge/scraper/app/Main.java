package id.ihaesge.scraper.app;

import java.util.List;

import id.ihaesge.scraper.api.ApiContentClient;
import id.ihaesge.scraper.core.Content;
import id.ihaesge.scraper.engine.NewsScraperEngine;
import id.ihaesge.scraper.sources.*;

public class Main {
    public static void main(String[] args) {
        int scrapLimit = readScrapeLimit(args);
//        String apiBaseUrl = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080/api");
        String fromSiteMap = System.getenv().getOrDefault("FROM_SITE_MAP", "FALSE");

        NewsScraperEngine engine = new NewsScraperEngine();
        registerSources(engine);

        engine.scrapeAll(scrapLimit, Boolean.valueOf(fromSiteMap).booleanValue());
        System.exit(0);
    }

    private static void registerSources(NewsScraperEngine engine) {
		engine.registerSource(new Bisnis());			//no sitemap
		engine.registerSource(new CNBC());				//no sitemap
		engine.registerSource(new EmitenNews());		//sitemap is done
		engine.registerSource(new EmitenTrust());		//sitemap is halfway done
		engine.registerSource(new Investor());			//sitemap is done
		engine.registerSource(new IPOTNews());			//no sitemap
		engine.registerSource(new KabarBursa());		//sitemap is halfway done 
		engine.registerSource(new KataData());			//sitemap is done
		engine.registerSource(new Kontan());			//no sitemap
		engine.registerSource(new Neraca());			//no sitemap
		engine.registerSource(new StockWatch());		//sitemap is done
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
