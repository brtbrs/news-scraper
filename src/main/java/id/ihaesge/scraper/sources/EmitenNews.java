package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

import com.microsoft.playwright.*;

public class EmitenNews extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.emitennews.com/category/emiten/";

    @Override
    public String getSourceName() {
        return "EMITENNEWS";
    }

    @Override
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        Element div = doc.selectFirst("div.main-layout");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.text());

            if (href.contains("/news/")) {
            	if (!href.startsWith("http")) {
            		href = "https://www.emitennews.com" + href;
            	}

            	if (!seen.contains(href)) {
            		seen.add(href);
            		list.add(new ArticleItem(title, href, getSourceName()));

            		if (scrapLimit > 0 && list.size() >= scrapLimit) break;
            	}
            }

            if (href.contains("/news/")) {
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

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            //<div class="article-body">
            Element div = doc.selectFirst("div.article-body");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                    !clean.contains("EmitenNews.com ")) {
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

    //<span class="time-posted">26/03/2026, 16:30 WIB</span>
    private LocalDateTime extractPublishDate(Document doc) {
        Element element = doc.selectFirst("span.time-posted");
        if (element != null) {
        	String[] parts = element.text().replaceAll("WIB", "").trim().split(",");
            if (parts.length == 2) {
            	String combine = parts[0].trim() + " " + parts[1].trim();
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.of("id", "ID"));
            	LocalDateTime ldt = LocalDateTime.parse(combine, formatter);
            	return ldt;
            }
        }

        return null;
    }

    private void removeNoiseEmitenNews(Document doc) {
        String[] selectors = {
                ".content-index-header", ".info-author", ".article-header-img", ".news-container.other-emiten-news-wrapper", ".recommendation-news-text"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }
}
