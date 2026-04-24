package id.ihaesge.scraper.app;

import java.util.List;

import id.ihaesge.scraper.api.ApiContentClient;
import id.ihaesge.scraper.core.Content;
import id.ihaesge.scraper.engine.NewsScraperEngine;
import id.ihaesge.scraper.sources.*;

public class Main {
	private static String AJAIB = "AJAIB";
	private static String BISNIS = "BISNIS";
	private static String CNBC = "CNBC";
	private static String EMITENNEWS = "EMITENNEWS";
	private static String EMITENTRUST = "EMITENTRUST";
	private static String INVESTOR = "INVESTOR";
	private static String IPOTNEWS = "IPOTNEWS";
	private static String KABARBURSA = "KABARBURSA";
	private static String KATADATA = "KATADATA";
	private static String KONTAN = "KONTAN";
	private static String NERACA = "NERACA";
	private static String STOCKWATCH = "STOCKWATCH";

    public static void main(String[] args) {
    	int scrapLimit = 2;
    	try {
    		scrapLimit = Integer.parseInt(readRequiredArg(args, "--limit="));    		
    	} catch(NumberFormatException e) {
    		System.out.println("Invalid --limit value. Falling back to default=" + scrapLimit);
    	}

    	String source = readRequiredArg(args, "--source=");
    	String from = readRequiredArg(args, "--from=");		//WEBSITE / SITEMAP

//        String apiBaseUrl = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080/api");
//        String fromSiteMap = System.getenv().getOrDefault("FROM_SITE_MAP", "FALSE");

    	if (source != null && from != null) {
            NewsScraperEngine engine = new NewsScraperEngine();
            boolean registered = registerSources(engine, source);
            if (registered) engine.scrape(scrapLimit, from);
    	}

//        engine.scrapeAll(scrapLimit, Boolean.valueOf(fromSiteMap).booleanValue());
        System.exit(0);
    }

    private static boolean registerSources(NewsScraperEngine engine, String source) {
    	boolean registered = true;

    	switch (source.toUpperCase()) {
	    	case "BISNIS":	    		
	    		engine.registerSource(new Bisnis());
	    		break;
	    	case "CNBC":
	    		engine.registerSource(new CNBC());
	    		break;
	    	case "EMITENNEWS":
	    		engine.registerSource(new EmitenNews());
	    		break;
	    	case "EMITENTRUST":
	    		engine.registerSource(new EmitenTrust());
	    		break;
	    	case "INVESTOR":
	    		engine.registerSource(new Investor());
	    		break;
	    	case "IPOTNEWS":
	    		engine.registerSource(new IPOTNews());
	    		break;
	    	case "KABARBURSA":
	    		engine.registerSource(new KabarBursa());
	    		break;
	    	case "KATADATA":
	    		engine.registerSource(new KataData());
	    		break;
	    	case "KONTAN":
	    		engine.registerSource(new Kontan());
	    		break;
	    	case "NERACA":
	    		engine.registerSource(new Neraca());
	    		break;
	    	case "STOCKWATCH":
	    		engine.registerSource(new StockWatch());
	    		break;
	    	case "ALL":
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
	    		break;
	    	default:
	    		System.out.println("=== UNRECOGNIZED SOURCE ===");
	    		registered = false;
	    		break;
    	}

    	return registered;
    }

//    private static int readScrapeLimit(String[] args) {
//        int defaultLimit = 2;
//        for (String arg : args) {
//            if (arg.startsWith("--limit=")) {
//                try {
//                    return Integer.parseInt(arg.substring("--limit=".length()));
//                } catch (NumberFormatException e) {
//                    System.out.println("Invalid --limit value. Falling back to default=" + defaultLimit);
//                }
//            }
//        }
//        return defaultLimit;
//    }

    private static String readRequiredArg(String[] args, String prefix) {
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("Missing argument: " + prefix + "...");
    }
}
