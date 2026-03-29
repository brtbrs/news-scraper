package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;

import com.microsoft.playwright.*;

public class Neraca extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.neraca.co.id/kategori/bursa-saham";

    @Override
    public String getSourceName() {
        return "NERACA";
    }

    @Override
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="terpopuler1" style="margin-top: 50px;">
        Element div = doc.selectFirst("div.terpopuler1");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.text());

            if (href.contains("article/")) {
            	if (!href.startsWith("http")) {
            		href = "https://www.neraca.co.id/" + href;
            	}

            	if (!seen.contains(href)) {
            		seen.add(href);
            		list.add(new ArticleItem(title, href, getSourceName()));

            		if (scrapLimit > 0 && list.size() >= scrapLimit) break;
            	}
            }
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
                if (clean != null && 
                		!clean.equalsIgnoreCase("NERACA")) {
                    content.append(clean);
                    if (!clean.isBlank()) content.append("\n");
                }
            }

            articleContent = new ArticleContent(title, ldt, content.toString(), url, getSourceName());
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

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy HH:mm:ss", Locale.of("id", "ID"));
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
}
