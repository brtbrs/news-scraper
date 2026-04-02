package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class StockWatch extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://stockwatch.id/";
	private final String BASE_URL_FINANSIAL = BASE_URL + "category/finansial/";
	private final String BASE_URL_MARKET = BASE_URL + "category/market/";

    @Override
    public String getSourceName() {
        return "STOCKWATCH";
    }

    @Override
    public List<ArticleItem> getArticleList(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL_MARKET).get();

        List<ArticleItem> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //market
        //<div id=tdi_91 class="td_block_inner">
        Element div = doc.selectFirst("div#tdi_91");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.attr("title"));

            if (href.contains(BASE_URL) && !href.startsWith(BASE_URL_MARKET) && !href.startsWith(BASE_URL + "author/")) {
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

        //market
        //<div id=tdi_97 class="td_block_inner tdb-block-inner td-fix-index">
        div = doc.selectFirst("div#tdi_97");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.attr("title"));

            if (href.contains(BASE_URL) && !href.startsWith(BASE_URL_MARKET) && !href.startsWith(BASE_URL + "author/")) {
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

        //finansial
        //<div id=tdi_33 class="td_block_inner">
        div = doc.selectFirst("div#tdi_33");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
            String title = cleanText(el.attr("title"));

            if (href.contains(BASE_URL) && !href.startsWith(BASE_URL_FINANSIAL) && !href.startsWith(BASE_URL + "author/")) {
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
//        	removeNoiseKataData(doc);

        	String title = cleanText(doc.selectFirst("meta[property=og:title]").attr("content"));
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder content = new StringBuilder();
            //<p style="text-align: justify">
            for (Element p : doc.select("p[style*=justify]")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                        !clean.contains("Disclaimer: ")) {
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

    //<meta property="og:updated_time" content="2026-03-31T21:32:13+07:00">
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[property=og:updated_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

//    private void removeNoiseStockWatch(Document doc) {
//        String[] selectors = {
////                ""
//        };
//
//        for (String sel : selectors) {
//            doc.select(sel).remove();
//        }
//    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	//be careful: \n at the end, dont forget to trim()
    	String[] PREFIX = {"STOCKWATCH.ID (JAKARTA) – ", "STOCKWATCH.ID (JAKARTA)– ", "STOCKWATCH.ID (JAKARTA) –", "STOCKWATCH.ID (JAKARTA)–"};	//must in order
    	String[] SUFFIX = {"(konrad)"};
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
