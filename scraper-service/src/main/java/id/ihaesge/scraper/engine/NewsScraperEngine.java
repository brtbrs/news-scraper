package id.ihaesge.scraper.engine;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import id.ihaesge.scraper.api.ApiContentClient;
import id.ihaesge.scraper.api.ApiPipelineLogClient;
import id.ihaesge.scraper.core.*;

public class NewsScraperEngine {
    private static final byte BREAK_LIMIT = 5;
    private static final String API_URL = "http://localhost:8080/api";
	private final List<NewsSource> sources = new ArrayList<>();

    public void registerSource(NewsSource source) {
        sources.add(source);
    }

    public void scrape(int scrapLimit, String from) {
        ApiContentClient apiContentClient = new ApiContentClient(API_URL);
        ApiPipelineLogClient apiPipelineLogClient = new ApiPipelineLogClient(API_URL);
        String[] processedURLs = {
        		};

        for (NewsSource source : sources) {
            List<String> found = new ArrayList<>();
            List<Content> saved = new ArrayList<>();
            List<String> skipped = new ArrayList<>();
            int count = 1;
            UUID pipelineLogId = null;

            try {
            	pipelineLogId = apiPipelineLogClient.createStartLog(source.getSourceName());

                System.out.println("===== START SCRAPING " + source.getSourceName() + " with limit : " + scrapLimit + " =====");
                found = source.getNewsList(scrapLimit, from);

                for (String newsURL : found) {		//newsList only contains url not full Content object
                	//this is to be able to resume scraping without re-scraping already processed urls (especially during scraping from sitemap.xml
                	//but this require manual work e.g. copying all the urls logged in the console in finally statement into the processedURLs array
                	//this needs to be done because some sources block scraper
                    boolean alreadyProcessed = false;
                	for (String processedURL : processedURLs) {
                		if (newsURL.equals(processedURL)) {
                			alreadyProcessed = true;
                			System.out.println("=== ALREADY PROCESSED === " + newsURL);
                			break;
                		}
                	}

                	if (!alreadyProcessed) {
                    	try {
                            System.out.println("\n***** get news detail : " + count++ + " ***** " + newsURL);
                            Content content = source.getNewsDetail(newsURL);

                            //sometimes we get 404 or empty content
                            if (content == null) {
                            	skipped.add(newsURL);
                            } else {
                            	if (content.getOriginalContent() == null || content.getOriginalContent().trim().length() == 0) {
                            		skipped.add(newsURL);
                            	} else {
    	                            int statusCode = apiContentClient.sendContent(content);
    	                            System.out.println("***** sending content : " + statusCode + " ***** " + content.toString());
    	                            if (statusCode < 200 || statusCode >= 300) {
    	                            	skipped.add(newsURL);
    	                            } else {
    	                            	saved.add(content);
    	                            }
                            	}
                            }
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    	} finally {
                        	if (skipped.size() > BREAK_LIMIT) break;		//let's assume, more than this means scraper is already blocked
                    	}

                    	//delay randomly
            		    //Thread.sleep(ThreadLocalRandom.current().nextInt(30000, 60000)); 
                	}
                }

                apiPipelineLogClient.updateFinishLog(pipelineLogId, found.size(), saved.size());
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	//printout scraping summary
                System.out.println("\n***** SCRAPING SUMMARY *****");
                System.out.println("TOTAL FOUND : " + found.size());

        		System.out.println("unPROCESSED URLs : " + skipped.size());
            	for (String unprocessedURL : skipped) {
            		System.out.println(unprocessedURL);
            	}

            	System.out.println("PROCESSED URLs : " + saved.size());
            	for (Content savedContent : saved) {
            		System.out.println(savedContent.getUrl());
            	}

                System.out.println("\n===== STOP SCRAPING " + source.getSourceName() + " =====");
            }
        }
    }
}
