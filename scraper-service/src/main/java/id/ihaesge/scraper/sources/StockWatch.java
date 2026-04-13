package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.microsoft.playwright.*;

public class StockWatch extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://stockwatch.id/";
	private final String FINANSIAL_URL = BASE_URL + "category/finansial/";
	private final String MARKET_URL = BASE_URL + "category/market/";
	private final String AUTHOR_URL = BASE_URL + "author/";

	private final String[] sitemap = {
//			"https://stockwatch.id/wp-sitemap-posts-post-1.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-2.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-3.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-4.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-5.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-6.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-7.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-8.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-9.xml",		//done
//			"https://stockwatch.id/wp-sitemap-posts-post-10.xml"		//done
			};

    @Override
    public String getSourceName() {
        return "STOCKWATCH";
    }

    @Override
    public List<String> getNewsList(int scrapLimit, boolean fromSiteMap) throws Exception {
    	List<String> urls = new ArrayList<>();

    	if (fromSiteMap) {
    		urls = getNewsListFromSiteMap(scrapLimit);
    	} else {
    		urls = getNewsListFromWebsite(scrapLimit);
    	}

    	return urls;
    }

    //in the sitemap, all url in <loc> is not categorized, so checking will be done in getNewsDetail (checking the breadcrumb)
    private List<String> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

    	for (String site : sitemap) {
    		
            Document doc = Jsoup.connect(site).get();
            for (Element url : doc.select("url")) {
                String href = url.selectFirst("loc").text().trim();

	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && urls.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			urls.add(href);	        			
	        		}
	        	}
            }
    	}

        return urls;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(MARKET_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //market
        //<div id=tdi_91 class="td_block_inner">
        Element div = doc.selectFirst("div#tdi_91");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.attr("title"));

            //<a href="https://stockwatch.id/bidik-pasar-digital-usd-29-miliar-dssa-garap-proyek-panas-bumi-hingga-infrastruktur-ai/"  rel="bookmark" 
            //<a href="https://stockwatch.id/author/daiz/">
            if (href.contains(BASE_URL) && !href.startsWith(MARKET_URL) && !href.startsWith(AUTHOR_URL)) {
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && urls.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			urls.add(href);	        			
	        		}
	        	}
            }
        }

        //market
        //<div id=tdi_97 class="td_block_inner tdb-block-inner td-fix-index">
        div = doc.selectFirst("div#tdi_97");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.attr("title"));

            if (href.contains(BASE_URL) && !href.startsWith(MARKET_URL) && !href.startsWith(AUTHOR_URL)) {
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && urls.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			urls.add(href);	        			
	        		}
	        	}
            }
        }

        //finansial
        //<div id=tdi_33 class="td_block_inner">
        div = doc.selectFirst("div#tdi_33");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.attr("title"));

            if (href.contains(BASE_URL) && !href.startsWith(FINANSIAL_URL) && !href.startsWith(AUTHOR_URL)) {
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && urls.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			urls.add(href);	        			
	        		}
	        	}
            }
        }

        return urls;
    }

    @Override
    public Content getNewsDetail(String url) throws Exception {
    	Content content = null;
    	try {
            Document doc = Jsoup.connect(normalizeUrl(url)).get();

        	//only extract the content if the category == "Market", check the breadcrumb
        	//<a title="Lihat semua kiriman dalam Market" class="tdb-entry-crumb">
        	boolean marketOrFinancial = false;
        	for (Element breadcumb : doc.select("a.tdb-entry-crumb")) {
        		String href = breadcumb.attr("href");
        		if (href.contains("market") || href.contains("finansial")) {
        			marketOrFinancial = true;
        			break;
        		}
        	}

        	if (marketOrFinancial) {
                content = extractContent(url, doc);

                if (content == null) {
                    System.out.println("[" + getSourceName() + "] Playwright fallback: " + url);
                    Playwright pw = Playwright.create();
                    Browser browser = pw.chromium().launch(
                    		new BrowserType.LaunchOptions().setHeadless(true)
                    );

                    Page page = browser.newPage();
                    page.navigate(normalizeUrl(url));
                    page.waitForTimeout(2000);

                    doc = Jsoup.parse(page.content());
                    content = extractContent(url, doc);
                    page.close();
                    browser.close();
                }
        	}
    	} catch (Exception e) {
    		e.printStackTrace();
		}

        return content;
    }

    private Content extractContent(String url, Document doc) {
    	Content newsContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseKataData(doc);

        	String title = cleanText(doc.selectFirst("meta[property=og:title]").attr("content"));
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //from website
            //<p style="text-align: justify">
            //for (Element p : doc.select("p[style*=justify]")) {

            //from site map
            //1. <p>, 2. <p style="text-align: justify;">, 3. <p class="entry-title td-module-title">
            //4. <p style="font-weight: 400;"> 
            //5. <p class="p1"> 6. <p class="p2"> 7. <p class="p3"> 8. <p class="s3"> 9. <p class="s6"> 9. <p class="s7"> 9. <p class="s9"> 9. <p class="s11"> 9. <p class="s12"> 10. <p class="s14"> 11. <p class="s13"> 12. <p class="s15"> 12. <p class="s16"> 13. 
            //13. <div> 14. <div class="s18"> 15. <div dir="ltr" style="text-align: justify;">
            //16. <p class="gmail-MsoNoSpacing"> 17. <p class="ng-star-inserted">
            Elements elements = doc.select("p:not([*]), p[style*=justify], p[style*=font-weight], p.p1, p.p2, p.p3, p.s3, p.s6, p.s7, p.s9, p.s11, p.s12, p.s13, p.s14, p.s15, p.s16, p.gmail-MsoNoSpacing, p.ng-star-inserted, div:not([*]), div.s18, div[style*=justify]");
            for (Element p : elements) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                        !clean.contains("Disclaimer: ")) {
                	sb.append(clean);
                    if (!clean.isBlank()) sb.append("\n");
                }
            }

            newsContent = new Content(title, ldt, removePrefixSuffix(sb.toString().trim()), url, getSourceName());
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return newsContent;
    }

    private LocalDateTime extractPublishDate(Document doc) {
        //<meta property="og:updated_time" content="2026-03-31T21:32:13+07:00">
        Element meta = doc.selectFirst("meta[property=og:updated_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseStockWatch(Document doc) {
        String[] selectors = {
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {
    			"(?i)^STOCKWATCH\\.ID\\s*\\(\\s*[^)]*\\s*\\)\\s*\\p{Pd}\\s*",		//"STOCKWATCH.ID (JAKARTA) – "
    			"(?i)^STOCKWATCH\\.ID,\\s*[^\\p{Pd}]+\\s*\\p{Pd}\\s*", 				//"STOCKWATCH.ID, Jakarta – "
    			"(?i)^STOCKWATCH\\.ID\\s*\\p{Pd}\\s*.*?\\s*\\p{Pd}\\s*", 			//"StockWatch.Id - Jakarta - "
    			"(?i)^STOCKWATCH\\.\\s*ID\\s*\\(\\s*[^)]*\\s*\\)\\s*\\p{Pd}\\s*", 	//"STOCKWATCH. ID (JAKARTA) – "
    			"(?i)^STOCWATCH\\.ID\\s*\\(\\s*[^)]*\\s*\\)\\s*\\p{Pd}\\s*",		//"STOCWATCH.ID (JAKARTA) – "
    			"(?i)^STOCKATCH\\.ID\\s*\\(\\s*[^)]*\\s*\\)\\s*\\p{Pd}\\s*",		//"STOCKATCH.ID (JAKARTA) – "
    			"(?i)^STOCKWTATCH\\.ID\\s*\\(\\s*[^)]*\\s*\\)\\s*\\p{Pd}\\s*"		//"STOCKWTATCH.ID (JAKARTA) – "
    			};

    	String[] SUFFIX = {
    			"(?i)\\(\\s*[^)]*\\s*\\)\\s*$"										//"(konrad)"
    			};

    	str.trim();																	//be careful: \n at the end, dont forget to trim()

    	if (str != null && str.length() > 0) {
        	for (String s : PREFIX) {
        		str = str.replaceFirst(s, "").trim();
//    	    	if (str.startsWith(s)) {
//    	    		str = str.substring(s.length()).trim();
//    	    		break;	//dont break because maybe have multiple prefixes
//    	    	}
        	}

        	for (String s : SUFFIX) {
        		str = str.replaceFirst(s, "").trim();
//    	    	if (str.endsWith(s)) {
//	    			str = str.substring(0, str.length() - s.length()).trim();
//    	    		break;	//break because maybe have only 1 suffix
//    	    	}
        	}
    	}

    	return str;
    }
}
