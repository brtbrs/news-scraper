package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class FAC extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.facsekuritas.co.id/news";
	private final String[] sitemap = {
	};

    @Override
    public String getSourceName() {
        return "FAC";
    }

    @Override
    public List<String> getNewsList(int scrapLimit, String from) throws Exception {
    	List<String> urls = new ArrayList<>();

    	if (from.equalsIgnoreCase(SITEMAP)) {
    		urls = getNewsListFromSiteMap(scrapLimit);
    	} else if (from.equalsIgnoreCase(WEBSITE)) {
    		urls = getNewsListFromWebsite(scrapLimit);
    	}

    	return urls;
    }

    //in the sitemap, all url in <loc> is not categorized, so checking will be done in getNewsDetail (checking the breadcrumb)
    private List<String> getNewsListFromSiteMap(int scrapLimit) throws Exception {
//    	List<String> urls = new ArrayList<>();
//        Set<String> seen = new HashSet<>();
//
//    	for (String site : sitemap) {    		
//            Document doc = Jsoup.connect(site).get();
//
//            for (Element loc : doc.select("loc")) {
//                String href = loc.text().trim();
//	        	if (!seen.contains(href)) {
//	        		seen.add(href);
//
//	        		if (scrapLimit > 0 && urls.size() >= scrapLimit) {
//	        			break;
//	        		} else {
//	        			urls.add(href);	        			
//	        		}
//	        	}
//            }
//    	}
//
//        return urls;
    	return null;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<h3><a href="https://www.facsekuritas.co.id/news/corporate-action/raja-ungkap-akuisisi-baru-simak-lengkapnya">RAJA Ungkap Akuisisi Baru, Simak Lengkapnya</a></h3>
        for (Element el : doc.select("h3")) {
            String href = el.attr("href");
//            String title = cleanText(el.text());

            //<a href="https://emitentrust.com/bank-mega-mega-tebar-dividen-jumbo-rp2t-ini-jadwalnya/"  rel="bookmark" 
            //<a href="https://emitentrust.com/author/komarudin/">
            if (href.contains(BASE_URL)) {
	        	if (!seen.contains(href)) {
	        		seen.add(href);

	        		if (scrapLimit > 0 && urls.size() >= scrapLimit) {
	        			break;
	        		} else {
	        			urls.add(href);	        			
	        		}
	        	}
            }
        }

        return urls;
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
    		e.printStackTrace();
		}

        return content;
    }

    private Content extractContent(String url, Document doc) {
    	Content articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseFAC(doc);

        	//<h2>RAJA Ungkap Akuisisi Baru, Simak Lengkapnya</h2>
        	String title = cleanText(doc.selectFirst("h2").text());
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

    private LocalDateTime extractPublishDate(Document doc) {
        //<div class="text-muted mb-4">Administrator - 11/05/2026 15:32</div>
        Element div = doc.selectFirst("div.text-muted.mb-4");
        if (div != null) {
            String publishDate = cleanText(div.text().split(" - ")[1]);

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseFAC(Document doc) {
        String[] selectors = {
                ".content-index-header", ".info-author", ".article-header-img", ".news-container.other-emiten-news-wrapper", ".recommendation-news-text"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {
    			};
    	String[] SUFFIX = {
    			"(?i)\\(\\s*[^)]*\\s*\\)\\s*$"										//"(*)", "( * )", "( * ) "
    			};
    	str.trim();																	//be careful: \n at the end, dont forget to trim()

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
