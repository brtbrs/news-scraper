package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import com.microsoft.playwright.*;

public class CNBC extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.cnbcindonesia.com/market/";

    @Override
    public String getSourceName() {
        return "CNBCINDONESIA";
    }

    @Override
    public List<String> getNewsList(int scrapLimit, String from) throws Exception {
    	List<String> urls = new ArrayList<>();

    	if (from.equalsIgnoreCase(SITEMAP)) {
    		urls = getNewsListFromSiteMap(scrapLimit);
    	} else if (from.equalsIgnoreCase(WEBSITE)) {
    		urls = getNewsListFromWebsite(scrapLimit);
    	}

    	return urls;
    }

    private List<String> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();


        return urls;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // ✅ MULTIPLE containers
        //<article>
        for (Element div : doc.select("article")) {

            // ✅ MULTIPLE links inside each container
            for (Element el : div.select("a[href]")) {
            	String href = el.attr("href");
            	if (!href.contains("video-")) {		//do not scrape if it is video news
//                    String title = cleanText(el.text());

            		//only get the "market" category
                	//<a class="group flex gap-4 items-center" href="https://www.cnbcindonesia.com/market/20260406102727-17-724106/ojk-ungkap-makna-saham-hsc-bukan-pelanggaran-tapi" dtr-evt="nhl" 
            		if (href.startsWith(BASE_URL)) {
//                    	if (!href.startsWith("http")) {
//                    		href = BASE_URL + href;
//                    	}

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
            }

            if (scrapLimit > 0 && urls.size() >= scrapLimit) break;
        }

        return urls;
    }

    @Override
    public Content getNewsDetail(String url) throws Exception {
    	Content content = null;
    	try {
            Document doc = Jsoup.connect(normalizeUrl(url)).get();
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
    	} catch (Exception e) {
    		e.printStackTrace();
		}

        return content;
    }

    private Content extractContent(String url, Document doc) {
    	Content articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseCNBC(doc);

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //<div class="detail-text">
            Element div = doc.selectFirst("div.detail-text");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null) {
//                    !clean.contains("Jakarta, CNBC Indonesia ")) {
                	sb.append(clean);
                    if (!clean.isBlank()) sb.append("\n");
                }
            }

            articleContent = new Content(title, ldt, removePrefixSuffix(sb.toString().trim()), url, getSourceName());
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return articleContent;
    }

    private LocalDateTime extractPublishDate(Document doc) {
        //<meta name="publishdate" content="2026/03/26 14:05:21" />
        Element meta = doc.selectFirst("meta[name=publishdate]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseCNBC(Document doc) {
        String[] selectors = {
                "figcaption", ".topik2"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {
    			"(?i)^Jakarta\\s*,\\s*CNBC Indonesia\\s*\\p{Pd}\\s*"				//"Jakarta , CNBC Indonesia — "
    			};
    	String[] SUFFIX = {};
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
    	    	if (str.endsWith(s)) {
	    			str = str.substring(0, str.length() - s.length()).trim();
    	    		break;	//break because maybe have only 1 suffix
    	    	}
        	}
    	}

    	return str;
    }
}
