package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class EmitenTrust extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://emitentrust.com/";
	private final String MARKET_URL = BASE_URL + "category/stock-and-market/";
	private final String AUTHOR_URL = BASE_URL + "author/";
	private final String[] sitemap = {
			"https://emitentrust.com/wp-sitemap-posts-post-1.xml"
	};

    @Override
    public String getSourceName() {
        return "EMITENTRUST";
    }

    @Override
    public List<Content> getNewsList(int scrapLimit, boolean fromSiteMap) throws Exception {
    	List<Content> list = new ArrayList<>();

    	if (fromSiteMap) {
    		list = getNewsListFromSiteMap(scrapLimit);
    	} else {
    		list = getNewsListFromWebsite(scrapLimit);
    	}

    	return list;
    }

    //in the sitemap, all url in <loc> is not categorized, so checking will be done in getNewsDetail (checking the breadcrumb)
    private List<Content> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

    	for (String site : sitemap) {    		
            Document doc = Jsoup.connect(site).get();

            for (Element loc : doc.select("loc")) {
                String href = loc.text().trim();
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && list.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			list.add(new Content(href, getSourceName()));	        			
	        		}
	        	}
            }
    	}

        return list;
    }

    private List<Content> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(MARKET_URL).get();

        List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div id=tdi_74 class="td_block_inner">
        Element div = doc.selectFirst("div#tdi_74");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.text());

            //<a href="https://emitentrust.com/bank-mega-mega-tebar-dividen-jumbo-rp2t-ini-jadwalnya/"  rel="bookmark" 
            //<a href="https://emitentrust.com/author/komarudin/">
            if (href.contains(BASE_URL) && !href.startsWith(MARKET_URL) && !href.startsWith(AUTHOR_URL)) {
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && list.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			list.add(new Content(href, getSourceName()));	        			
	        		}
	        	}
            }
        }

        //<div id=tdi_80 class="td_block_inner tdb-block-inner td-fix-index">
        div = doc.selectFirst("div#tdi_80");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.text());

            if (href.contains(BASE_URL) && !href.startsWith(MARKET_URL) && !href.startsWith(AUTHOR_URL)) {
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && list.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			list.add(new Content(href, getSourceName()));	        			
	        		}
	        	}
            }
        }


        return list;
    }

    @Override
    public Content getNewsDetail(String url) {
    	Content content = null;
    	try {
            Document doc = Jsoup.connect(normalizeUrl(url)).get();

        	//only extract the content if the category == "Market", check the breadcrumb
            //<a title="" class="tdb-entry-crumb">
        	boolean stockAndMarket = false;
        	for (Element breadcumb : doc.select("a.tdb-entry-crumb")) {
        		String href = breadcumb.attr("href");
        		if (href.contains("/category/stock-and-market/")) {
        			stockAndMarket = true;
        			break;
        		}
        	}

        	if (stockAndMarket) {
        		content = extractContent(url, doc);

//                if (content == null) {
//                    System.out.println("[" + getSourceName() + "] Playwright fallback: " + url);
//                    Playwright pw = Playwright.create();
//                    Browser browser = pw.chromium().launch(
//                    		new BrowserType.LaunchOptions().setHeadless(true)
//                    );
//
//                    Page page = browser.newPage();
//                    page.navigate(normalizeUrl(url));
//                    page.waitForTimeout(2000);
//
//                    doc = Jsoup.parse(page.content());
//                    content = extractContent(url, doc);
//                    page.close();
//                    browser.close();
//                }
        	}

    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}

        return content;
    }

    private Content extractContent(String url, Document doc) {
    	Content articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseKataData(doc);

        	//<meta property="og:title" content="Melejit 2.744%, Laba Bank Neo (BBYB) Capai Rp565M di 2025">
        	String title = cleanText(doc.selectFirst("meta[property=og:title]").attr("content"));
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            for (Element p : doc.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null && 
//                    !clean.contains("Emitentrust.com ") && 
                    !clean.contains("- EmitenTrust")) {
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

//    private void removeNoiseEmitenTrust(Document doc) {
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
//    	String[] PREFIX = {"Emitentrust.com"};	//must in order
    	String[] PREFIX = {"(?i)^emitentrust.com\\s*\\p{Pd}\\s*"};
    	String[] SUFFIX = {};
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
