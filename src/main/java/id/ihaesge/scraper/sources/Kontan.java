package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

import com.microsoft.playwright.*;

public class Kontan extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://investasi.kontan.co.id/";

    @Override
    public String getSourceName() {
        return "KONTAN";
    }

    @Override
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        Element div = doc.selectFirst("div.list-berita");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.text());

            //<a href="https://investasi.kontan.co.id/news/ihsg-melemah-056-dalam-sepekan-simak-proyeksinya-untuk-senin-3032026">
            if (href.contains("news/")) {
            	if (!href.startsWith("http")) {
            		href = BASE_URL + href;
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

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            //<div class="tmpt-desk-kon" itemprop="articleBody">
            Element div = doc.selectFirst("div.tmpt-desk-kon[itemprop=articleBody]");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null && 
                		!clean.contains("Reporter:") && 
                		!clean.contains("KONTAN.CO.ID - ") && 
                		!clean.contains("Cek Berita dan Artikel yang lain")) {
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

    //<meta name="content_PublishedDate" content="2026-03-26 12:14:10" />
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[name=content_PublishedDate]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.of("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

            return ldt;
        }

        return null;
    }

//    private LocalDateTime extractDateTime(String text) {
//        //Pattern p = Pattern.compile(".*?(\\d{1,2}\\s\\w+\\s\\d{4})\\s*/\\s*(\\d{2}:\\d{2}).*");
//        Pattern p = Pattern.compile(".*?(\\d{1,2}\\s(?:Januari|Februari|Maret|April|Mei|Juni|Juli|Agustus|September|Oktober|November|Desember)\\s\\d{4})\\s*/\\s*(\\d{2}:\\d{2})");
//        Matcher m = p.matcher(text);
//
//        if (m.find()) {
//        	String combined = m.group(1) + " " + m.group(2);
//        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.of("id", "ID"));
//        	LocalDateTime ldt = LocalDateTime.parse(combined, formatter);
//
//        	return ldt;
//        }
//        return null;
//    }

    private void removeNoiseKontan(Document doc) {
        String[] selectors = {
                ".track-bacajuga-inside", ".track-gnews"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }
}
