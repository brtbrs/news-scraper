package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class Bisnis extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.bisnis.com/index?categoryId=194";

    @Override
    public String getSourceName() {
        return "BISNIS";
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

    private List<Content> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();


        return list;
    }

    private List<Content> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="artContent">
        for (Element div : doc.select("div.artContent")) {
        	Element el = div.selectFirst("a[href]");
            String href = el.attr("href");
//            String title = cleanText(el.text());

            //only get "market" category
            //<a href="https://market.bisnis.com/read/20260329/192/1962766/menilik-postur-keuangan-di-balik-rekor-laba-hartadinata-hrta-sepanjang-2025" class="artLink">
            if (href.startsWith("https://market.bisnis.com/read/")) {
//            	if (!href.startsWith("https://market.bisnis.com/read/")) {
//            		href = "https://market.bisnis.com" + href;
//            	}

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
            content = extractContent(url, doc);

            if (content == null) {
                System.out.println("[" + getSourceName() + "] Playwright fallback: " + url);
                Playwright pw = Playwright.create();
                Browser browser = pw.chromium().launch(
                		new BrowserType.LaunchOptions().setHeadless(true)
                );

                Page page = browser.newPage();
                page.navigate(normalizeUrl(url));
                page.waitForTimeout(2000);

                doc = Jsoup.parse(page.content());
                content = extractContent(url, doc);
                page.close();
                browser.close();
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
//        	removeNoiseBisnis(doc);

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //<article class="detailsContent force-17 mt40">
            Element div = doc.selectFirst("article.detailsContent");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
                		!clean.contains("Disclaimer") && 
                		!clean.contains("Baca Juga")) {
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

    //<meta name="publishdate" content="2026/03/25 15:11:47" />
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

//    private void removeNoiseBisnis(Document doc) {
//        String[] selectors = {
//                ".skyscrapper", ".bisnisaiHeader", ".bisnisaiBody", ".bisnisaiFooter", ".baca-juga-box", ".detailsAuthor", ".billboardWrapper"
//        };
//
//        for (String sel : selectors) {
//            doc.select(sel).remove();
//        }
//    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	//be careful: \n at the end, dont forget to trim()
//    	String[] PREFIX = {"Bisnis.com, JAKARTA —", "Bisnis.com, JAKARTA—", "Bisnis.com,JAKARTA —", "Bisnis.com,JAKARTA—", "Bisnis.com , JAKARTA —", "Bisnis.com ,JAKARTA —", "Bisnis.com ,JAKARTA—"};	//must in order
    	String[] PREFIX = {"(?i)^Bisnis.com\\s*,\\s*jakarta\\s*\\p{Pd}\\s*"};
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
