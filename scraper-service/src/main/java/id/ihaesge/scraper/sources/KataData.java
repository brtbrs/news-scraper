package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class KataData extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://katadata.co.id/finansial/bursa/";

    @Override
    public String getSourceName() {
        return "KATADATA";
    }

    @Override
    public List<Content> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="latest-news result">
        Element div = doc.selectFirst("div.latest-news.result");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.text());

            //<a href="https://katadata.co.id/finansial/bursa/69cf770c1292f/bumn-karya-kian-mengkhawatirkan-rugi-ptpp-dan-wika-bengkak-berkali-kali-lipat">
            //if (href.contains("/finansial/bursa/")) {
            if (href.startsWith(BASE_URL)) {
            	if (!seen.contains(href)) {
            		seen.add(href);

	        		if (scrapLimit > 0 && list.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			list.add(new Content(title, href, getSourceName()));	        			
	        		}
            	}
            }
        }

        return list;
    }

    @Override
    public Content getContent(String url) {
    	Content article = null;
    	try {
            Document doc = Jsoup.connect(normalizeUrl(url)).get();
            article = extractContent(url, doc);

            if (article == null) {
                System.out.println("[" + getSourceName() + "] Playwright fallback: " + url);
                Playwright pw = Playwright.create();
                Browser browser = pw.chromium().launch(
                		new BrowserType.LaunchOptions().setHeadless(true)
                );

                Page page = browser.newPage();
                page.navigate(normalizeUrl(url));
                page.waitForTimeout(2000);

                doc = Jsoup.parse(page.content());
                article = extractContent(url, doc);
                page.close();
                browser.close();
            }
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}

        return article;
    }

    private Content extractContent(String url, Document doc) {
    	Content articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseKataData(doc);

        	//<h1 class="detail-title mb-4">
        	String title = cleanText(doc.selectFirst("h1.detail-title.mb-4").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            //<div class="detail-body mb-4">
            Element div = doc.selectFirst("div.detail-body.mb-4");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                    !clean.contains("Dapatkan pengalaman membaca")) {
                	content.append(clean);
                    if (!clean.isBlank()) content.append("\n");
                }
            }

            articleContent = new Content(title, ldt, removePrefixSuffix(content.toString().trim()), url, getSourceName());
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return articleContent;
    }

    //<meta property="article:published_time" content="2026-03-31 17:13:00">
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[property=article:published_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

//    private void removeNoiseKataData(Document doc) {
//        String[] selectors = {
//                ".content-index-header", ".info-author", ".article-header-img", ".news-container.other-emiten-news-wrapper", ".recommendation-news-text"
//        };
//
//        for (String sel : selectors) {
//            doc.select(sel).remove();
//        }
//    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	//be careful: \n at the end, dont forget to trim()
    	String[] PREFIX = {};	//must in order
    	String[] SUFFIX = {};
    	str.trim();

    	if (str != null && str.length() > 0) {
        	for (String s : PREFIX) {
    	    	if (str.startsWith(s)) {
    	    		str = str.substring(s.length()).trim();
//    	    		break;	//dont break because maybe have multiple prefixes
    	    	}
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
