package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class KabarBursa extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.kabarbursa.com/market-hari-ini/";
	private final String[] sitemap = {
			"https://www.kabarbursa.com/sitemap-news.xml"
	};

    @Override
    public String getSourceName() {
        return "KABARBURSA";
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

    //in the sitemap, all url in <loc> is categorized, so checking will be done here
    private List<Content> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

    	for (String site : sitemap) {    		
            Document doc = Jsoup.connect(site).get();

            for (Element loc : doc.select("loc")) {
                String href = loc.text().trim();
            	if (href.contains("market-hari-ini")) {
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
    	}

        return list;
    }

    private List<Content> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="space-y-6">
        Element div = doc.selectFirst("div.space-y-6");
        for (Element el : div.select("a")) {
            String href = el.attr("href");

            //<a href="https://www.kabarbursa.com/market-hari-ini/ritel-teratas-apa-saja-yang-diborong-sepekan"
        	if (href.startsWith(BASE_URL)) {
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
    public Content getNewsDetail(String url) throws Exception {
    	Content content = null;
    	try {
            Document doc = Jsoup.connect(normalizeUrl(url)).get();

            //<h1 style="font-size: 72px; font-weight: 700;">404</h1>
            Element four0four = doc.selectFirst("h1");
            if (four0four != null && four0four.text().equals("404")) {
            	System.out.println("404404404404404404404404404404");
            } else {
                content = extractContent(url, doc);

//              if (content == null) {
//                  System.out.println("[" + getSourceName() + "] Playwright fallback: " + url);
//                  Playwright pw = Playwright.create();
//                  Browser browser = pw.chromium().launch(
//                  		new BrowserType.LaunchOptions().setHeadless(true)
//                  );

//                  Page page = browser.newPage();
//                  page.navigate(normalizeUrl(url));
//                  page.waitForTimeout(2000);

//                  doc = Jsoup.parse(page.content());
//                  content = extractContent(url, doc);
//                  page.close();
//                  browser.close();
//              }
            }
    	} catch (Exception e) {
			// TODO: handle exception
//    		e.printStackTrace();
    		throw e;
		}

        return content;
    }

    private Content extractContent(String url, Document doc) throws Exception {
    	Content articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseKataData(doc);

        	//<meta property="og:title" content="SOHO Amankan Kredit BCA Rp150 Miliar, Buat Apa?">
        	String title = cleanText(doc.selectFirst("meta[property=og:title]").attr("content"));
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //<div id="articleContent" class="prose max-w-none text-gray-800 mt-6">
            Element div = doc.selectFirst("div#articleContent");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                    !clean.contains("Dapatkan pengalaman membaca")) {
                	sb.append(clean);
                    if (!clean.isBlank()) sb.append("\n");
                }
            }

            articleContent = new Content(title, ldt, removePrefixSuffix(sb.toString().trim()), url, getSourceName());
        } catch (Exception e) {
//        	e.printStackTrace();
        	throw e; 
        }

        return articleContent;
    }

    //<meta property="article:published_time" content="2026-04-04T19:00:00+07:00">
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[property=article:published_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

//    private void removeNoiseKabarBursa(Document doc) {
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
//    	String[] PREFIX = {"KABARBURSA.COM - ", "KABARBURSA.COM -", "KABARBURSA.COM- ", "KABARBURSA.COM-", 
//    						"KABARBURSA.COM – ", "KABARBURSA.COM –", "KABARBURSA.COM– ", "KABARBURSA.COM–"};	//must in order
    	String[] PREFIX = {"(?i)^KABARBURSA\\.C[O0]M\\s*\\p{Pd}\\s*"};
//    	String[] SUFFIX = {"(*)"};
    	String[] SUFFIX = {"(?i)\\(\\s*[^)]*\\s*\\)\\s*$"};
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
        		str = str.replaceFirst(s, "").trim();
//    	    	if (str.endsWith(s)) {
//	    			str = str.substring(0, str.length() - s.length()).trim();
//    	    		break;	//break because maybe have only 1 suffix
//    	    	}
        	}
    	}

    	return str;
    }
}
