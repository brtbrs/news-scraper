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
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // ✅ MULTIPLE containers
        for (Element div : doc.select("article")) {

            // ✅ MULTIPLE links inside each container
            for (Element el : div.select("a[href]")) {
            	String href = el.attr("href");
                String title = cleanText(el.text());

                if (href.contains("/market/") && href.matches(".*\\d{14}-\\d+-.*")) {
                	if (!href.startsWith("http")) {
                		href = BASE_URL + href;
                	}

                	if (!seen.contains(href)) {
                		seen.add(href);

                		if (scrapLimit > 0 && list.size() >= scrapLimit) {
    	        			break;
    	        		} else {
    	        			list.add(new ArticleItem(title, href, getSourceName()));	        			
    	        		}
                	}
                }
            }

            if (scrapLimit > 0 && list.size() >= scrapLimit) break;
        }

        return list;
    }

    @Override
    public ArticleContent getArticleContent(String url) {
    	ArticleContent article = null;
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

    private ArticleContent extractContent(String url, Document doc) {
    	ArticleContent articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseCNBC(doc);

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            //<div class="detail-text">
            Element div = doc.selectFirst("div.detail-text");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null) {
//                    !clean.contains("Jakarta, CNBC Indonesia ")) {
                	content.append(clean);
                    if (!clean.isBlank()) content.append("\n");
                }
            }

            articleContent = new ArticleContent(title, ldt, removePrefixSuffix(content.toString().trim()), url, getSourceName());
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return articleContent;
    }

    //<meta name="publishdate" content="2026/03/26 14:05:21" />
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[name=publishdate]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

//    private void removeNoiseCNBC(Document doc) {
//        String[] selectors = {
//                "figcaption", ".topik2"
//        };
//
//        for (String sel : selectors) {
//            doc.select(sel).remove();
//        }
//    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	//be careful: \n at the end, dont forget to trim()
    	String[] PREFIX = {"Jakarta, CNBC Indonesia —", "Jakarta, CNBC Indonesia — ", "Jakarta, CNBC Indonesia— ", "Jakarta, CNBC Indonesia—"};	//must in order
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
