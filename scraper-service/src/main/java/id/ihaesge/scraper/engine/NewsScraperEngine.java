package id.ihaesge.scraper.engine;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import id.ihaesge.scraper.api.ApiContentClient;
import id.ihaesge.scraper.core.*;

public class NewsScraperEngine {
	private final List<NewsSource> sources = new ArrayList<>();

    public void registerSource(NewsSource source) {
        sources.add(source);
    }

    public List<Content> scrapeAll(int scrapLimit, boolean fromSitemMap) {
        ApiContentClient apiContentClient = new ApiContentClient("http://localhost:8080/api");
        List<Content> results = new ArrayList<>();
        List<Content> emptyContent = new ArrayList<>();
        List<Content> four0four = new ArrayList<>();
        String[] urls = {
        		};

        for (NewsSource source : sources) {
            int count = 1;
            int exceptionCount = 0;
            int four0fourCount = 0;

            try {
                List<Content> newsList = source.getNewsList(scrapLimit, fromSitemMap);
//                System.out.println("\n===== " + source.getSourceName() + " : " + urls.length + " =====");
                System.out.println("\n===== " + source.getSourceName() + " : " + newsList.size() + " =====");

                for (Content item : newsList) {		//newsList only contains url not full Content object
                    boolean alreadyProcessed = false;
                	for (String url : urls) {
                		if (item.getUrl().equals(url)) {
                			alreadyProcessed = true;
                			System.out.println("=== ALREADY PROCESSED === " + item.getUrl());
                			break;
                		}
                	}

                	if (!alreadyProcessed) {
                    	try {
                            System.out.println("\n***** get content : " + count++ + " ***** " + item.getUrl());
//                            System.out.println("\n***** get content : " + count++ + " ***** " + url);
                            Content content = source.getNewsDetail(item.getUrl());
//                            Content content = source.getNewsDetail(url);

                            if (content == null) {
                            	four0four.add(new Content(item.getUrl(), source.getSourceName()));
//                            	four0four.add(new Content(url, source.getSourceName()));
                            	four0fourCount++;
                            	if (four0fourCount > 5) break;
                            } else {
                            	if (content.getOriginalContent() == null || content.getOriginalContent().trim().length() == 0) {
                            		emptyContent.add(content);
                            	} else {
    	                            results.add(content);
    	                            System.out.println("\n***** sending content *****" + content.toString());
    	                            apiContentClient.sendContent(content);
                            	}
                            }
                    	} catch (Exception e) {
                    		System.out.println("=== EXCEPTION : " + item.getUrl());
                    		e.printStackTrace();
                    		exceptionCount++;
                    		if (exceptionCount > 5) break;
                    	} finally {
                    	}

                    	//delay 1 minute
//                    	if (count % (ThreadLocalRandom.current().nextInt(50, 100)) == 0) {
//                    		try {
//                    		    Thread.sleep(ThreadLocalRandom.current().nextInt(30000, 60000)); 
//                    		} catch (InterruptedException e) {
//                    			e.printStackTrace();
//                    		    // Handle or rethrow the exception
//                    		} finally {
//                    			System.out.println("=== KELAR DELAY ===");
//                    		}
//                    	}
                	}
                }
            } catch (Exception e) {
                System.out.println("Error in source: " + source.getSourceName());
            	e.printStackTrace();
            } finally {
        		System.out.println("\n***** PROCESSED URLs : " + results.size() + "\n");
            	for (Content item : results) {
            		System.out.println(item.getUrl());
            	}

        		System.out.println("\n***** 404 content : " + four0four.size() + "\n");
            	for (Content item : four0four) {
            		System.out.println(item.toString());
            	}

            	System.out.println("=== EXCEPTION : " + exceptionCount);
            }
        }

        return results;
    }
}
