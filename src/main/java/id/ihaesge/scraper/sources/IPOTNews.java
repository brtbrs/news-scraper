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

public class IPOTNews extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.ipotnews.com/ipotnews/";

    @Override
    public String getSourceName() {
        return "IPOT";
    }

    @Override
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // ✅ MULTIPLE containers
        for (Element div : doc.select("div.keeptogether")) {

            // ✅ MULTIPLE links inside each container
            for (Element el : div.select("a[href]")) {
            	String href = el.attr("href");
                String title = cleanText(el.text());

                if (href.contains("newsDetail.php")) {
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
            String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            //<article>
            Element article = doc.selectFirst("article");
            String[] brs = article.html().split("<br>");
            StringBuilder content = new StringBuilder();

            for (int i=0; i<brs.length; i++) {
                if (!brs[i].contains("Sumber :") &&
                	!brs[i].contains("(reuters)")) {
                	String clean = cleanText(brs[i]);
                    if (!clean.isBlank()) {
                    	System.out.println("brs[" + i + "] : " + clean);
                    	content.append(clean);
                    	content.append("\n");
                    }
                }
            }

            articleContent = new ArticleContent(title, ldt, content.toString(), url, getSourceName());
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return articleContent;
    }

    //<meta property="article:published_time" content="2026-03-27 14:52:59">
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[property=article:published_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.of("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseIPOTNews(Document doc) {
        String[] selectors = {
        		//article list
        		//article detail
                "#stockInfoContainer", "#popInfoModal", "#OneClickContainer", "#lmDanaTersediaFund", "#lmDanaTersediaStock", "#lmDanaTersediaTalangan", "#lmDanaTersediaTalanganRDPU", "#lmLimitFasilitasMin", "#lmLimitFasilitasMax", "#lmDanaTersediaTalanganFasilitasFund", "lmDanaTersediaTalanganFasilitasStockMin", "#lmDanaTersediaTalanganFasilitasStockMax", "#lmDanaTersediaTalanganFasilitasStockMax",
                //common
                "#login_modal", "#showhidebasket", "#navnews", "#navbarIpotnews", ".sidebar.sidebar-right", 
                "#infoLimit", "#infoOdt", "#infoMargin", "#upgradeFasilitas", "#confirmFasilitas", "#searchBarContainer"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }
}
