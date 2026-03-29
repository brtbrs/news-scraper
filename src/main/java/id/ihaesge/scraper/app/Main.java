package id.ihaesge.scraper.app;

import id.ihaesge.scraper.core.*;
import id.ihaesge.scraper.engine.*;
import id.ihaesge.scraper.sources.*;

import java.util.*;

public class Main {
	public static void main(String[] args) {
		NewsScraperEngine engine = new NewsScraperEngine();
	
	    // ✅ Plug sources here	engine.
	    engine.registerSource(new Bisnis());
	    //engine.registerSource(new CNBC());
		//engine.registerSource(new EmitenNews());
		//engine.registerSource(new Investor());
		//engine.registerSource(new IPOTNews());
	    //engine.registerSource(new Kontan());
		//engine.registerSource(new Neraca());
	
	    int scrapLimit = 2; // debug
	
	    List<ArticleContent> results = engine.scrapeAll(scrapLimit);
	
	    System.out.println("*************** START ***************");
	    for (ArticleContent a : results) {
	        System.out.println("=================================");
	        System.out.println("Source: " + a.source);
	        System.out.println("Title: " + a.title);
	        System.out.println("Date: " + a.publishDate);
	        System.out.println("URL: " + a.url);
	        System.out.println("Content: " + a.content);
	        //System.out.println("Preview: " + a.content.substring(0, Math.min(200, a.content.length())));
	    }
	    System.out.println("*************** END ***************");
	}
}
