package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

public class Investor extends BaseScraper implements NewsSource {
    private final String BASE_URL = "https://investor.id/market/";

    @Override
    public String getSourceName() {
        return "INVESTOR";
    }

    @Override
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        Element div = doc.selectFirst("main");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.text());

            if (href.contains("/market/")) {
            	if (!href.startsWith("http")) {
            		href = "https://investor.id" + href;
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
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseKontan(doc);

            // DETECT MULTI-PAGE
            if (isMultiPage(doc)) {
                String allUrl = toAllPageUrl(url);

                try {
                    doc = Jsoup.connect(allUrl).get();
                    url = allUrl; // update reference
                } catch (Exception e) {
                	e.printStackTrace();
                    // fallback to original if /all fails
                }
            }

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            //<div class="tmpt-desk-kon" itemprop="articleBody">
            Element div = doc.selectFirst("div.body-content");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                        !clean.contains("JAKARTA, investor.id ") &&  
                    	!clean.contains("Baca Berita Lainnya") &&  
                    	!clean.contains("Follow Channel") &&  
                    	!clean.contains("Baca Juga:") &&  
                		!clean.contains("Editor:")) {
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

    // JSON-LD PARSER
    //"datePublished": "2026-03-26T15:47:00+07:00"
    private LocalDateTime extractPublishDate(Document doc) {
    	Elements scripts = doc.select("script[type=application/ld+json]");
        for (Element script : scripts) {
            String json = script.data().trim();
            Pattern p = Pattern.compile("\"datePublished\"\\s*:\\s*\"(.*?)\"");
            Matcher m = p.matcher(json);

            if (m.find()) {
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.of("id", "ID"));
            	LocalDateTime ldt = LocalDateTime.parse(m.group(1), formatter);

            	return ldt;
            }
    	}

        return null;
    }

    private void removeNoiseInvestor(Document doc) {
        String[] selectors = {
                ".modal.fade"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private boolean isMultiPage(Document doc) {
        return doc.text().contains("Halaman:");
    }
 
    private String toAllPageUrl(String url) {
        if (url.endsWith("/all")) return url;
        return url + "/all";
    }
}