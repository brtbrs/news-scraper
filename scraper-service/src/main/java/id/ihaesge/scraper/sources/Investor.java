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
    private final String BASE_URL = "https://investor.id";
    private final String MARKET_URL = BASE_URL + "/market";
	private final String[] sitemap = {
//			"https://investor.id/sitemap_news.xml",		//done
//			"https://investor.id/sitemap_post.xml"		//done
	};

    @Override
    public String getSourceName() {
        return "INVESTOR";
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

    //in the sitemap, all url in <loc> is categorized, so checking will be done here
    private List<String> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

    	for (String site : sitemap) {    		
            Document doc = Jsoup.connect(site).get();

            for (Element loc : doc.select("loc")) {
                String href = loc.text().trim();
            	if (href.contains("/market/")) {
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
    	}

        return urls;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(MARKET_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        Element div = doc.selectFirst("main");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.text());

            //<a href="/market/434116/prasyarat-berat-merger-bumn-karya" class="stretched-link">
            //ada /corporate-action/, /stock/, /crypto/, dll, tapi jika di-click larinya ke /market"
            if (href.contains("/market/") && href.length() > (MARKET_URL + "/indeks").length()) {
            	if (!href.startsWith("http")) {
            		href = BASE_URL + href;
            	}

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
    public Content getNewsDetail(String url) throws Exception {
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
//    	url = "https://investor.id/market/434726/harga-perak-antam-antm-hari-ini-jumat-10-april-2026-naikperkasa";
    	Content articleContent = null;
        try {
        	//no need to remove noise because extraction only on specific part (selectFirst)
//        	removeNoise(doc);
//        	removeNoiseKontan(doc);

            // DETECT MULTI-PAGE
            if (isMultiPage(doc)) {
                url = toAllPageUrl(url);

                try {
                    doc = Jsoup.connect(url).get();
                } catch (Exception e) {
                	e.printStackTrace();
                    // fallback to original if /all fails
                }
            }

        	String title = cleanText(doc.selectFirst("h1").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //<div class="col fsbody2 body-content">
            Element div = doc.selectFirst("div.body-content");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null &&
//                        !clean.contains("JAKARTA, investor.id ") &&  
                    	!clean.contains("Baca Berita Lainnya") &&  
                    	!clean.contains("Follow Channel") &&  
                    	!clean.contains("Baca Juga:") &&  
                		!clean.contains("Editor:")) {
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
        // JSON-LD PARSER
        //"datePublished": "2026-03-26T15:47:00+07:00"
    	Elements scripts = doc.select("script[type=application/ld+json]");
        for (Element script : scripts) {
            String json = script.data().trim();
            Pattern p = Pattern.compile("\"datePublished\"\\s*:\\s*\"(.*?)\"");
            Matcher m = p.matcher(json);

            if (m.find()) {
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", new Locale("id", "ID"));
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

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {
    			"(?i)^JAKARTA\\s*,\\s*investor.id\\s*\\p{Pd}\\s*"					//"JAKARTA , investor.id - "
    			};
    	String[] SUFFIX = {};
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