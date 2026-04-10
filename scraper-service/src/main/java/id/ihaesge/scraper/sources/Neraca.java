package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class Neraca extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.neraca.co.id/kategori/bursa-saham";

    @Override
    public String getSourceName() {
        return "NERACA";
    }

    @Override
    public List<Content> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="terpopuler1" style="margin-top: 50px;">
        Element div = doc.selectFirst("div.terpopuler1");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.text());

            //<a href="/article/235159/antam-cetak-laba-bersih-rp-792-triliun" rel="bookmark">
            if (href.contains("article/")) {
            	if (!href.startsWith("http")) {
            		href = "https://www.neraca.co.id/" + href;
            	}

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
        	//need to remove noise because there are other parts that use <p> that are not relevant
        	removeNoise(doc);
        	removeNoiseNeraca(doc);

            Element h1 = doc.selectFirst("h1");
            if (h1 == null) return null;

            String title = cleanText(h1.text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            for (Element p : doc.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null) { 
//                		!clean.equalsIgnoreCase("NERACA")) {
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

    //<span>Kamis, 26/03/2026</span>
    private LocalDateTime extractPublishDate(Document doc) {
        Element el = doc.selectFirst("span:matches(^\\w+,\\s\\d{2}/\\d{2}/\\d{4}$)");
        if (el != null) {
            String publishDate = cleanText(el.text() + " 00:00:01");

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy HH:mm:ss", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseNeraca(Document doc) {
        String[] selectors = {
        		//article list
        		//article detail
                ".berita-terkait", ".terpopuler1"
                //common
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	//be careful: \n at the end, dont forget to trim()
//    	String[] PREFIX = {"NERACA", "Jakarta - ", "Jakarta- ", "Jakarta -", "Jakarta-"};	//must in order
    	String[] PREFIX = {"NERACA", "(?i)^Jakarta\\s*\\p{Pd}\\s*"};
    	String[] SUFFIX = {"(bani)"};
    	str.trim();

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
