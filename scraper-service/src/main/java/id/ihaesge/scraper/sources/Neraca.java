package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class Neraca extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.neraca.co.id";
	private final String SAHAM_URL = BASE_URL + "/kategori/bursa-saham";

    @Override
    public String getSourceName() {
        return "NERACA";
    }

    @Override
    public List<String> getNewsList(int scrapLimit, boolean fromSiteMap) throws Exception {
    	List<String> urls = new ArrayList<>();

    	if (fromSiteMap) {
    		urls = getNewsListFromSiteMap(scrapLimit);
    	} else {
    		urls = getNewsListFromWebsite(scrapLimit);
    	}

    	return urls;
    }

    private List<String> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();


        return urls;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(SAHAM_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //Berita Terpopuler
        //<div class="terpopuler1" style="margin-top: 50px;">
//      Element div = doc.selectFirst("div.terpopuler1");

        //Rubrik Bursa Saham
        //<h4 class="judul">
        for (Element el : doc.select("h4.judul")) {
        	Element a = el.selectFirst("a[href]");
            String href = a.attr("href");

            //<a href="/article/235159/antam-cetak-laba-bersih-rp-792-triliun" rel="bookmark">
            if (href.contains("/article/")) {
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
    	Content articleContent = null;
        try {
        	//need to remove noise because there are other parts that use <p> that are not relevant
        	removeNoise(doc);
        	removeNoiseNeraca(doc);

            Element h1 = doc.selectFirst("h1");
            if (h1 == null) return null;

            String title = cleanText(h1.text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            for (Element p : doc.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null) { 
//                		!clean.equalsIgnoreCase("NERACA")) {
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
        //<span>Kamis, 26/03/2026</span>
        Element el = doc.selectFirst("span:matches(^\\w+,\\s\\d{2}/\\d{2}/\\d{4}$)");
        if (el != null) {
            String publishDate = cleanText(el.text() + " 00:00:01");

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy HH:mm:ss", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseNeraca(Document doc) {
        String[] selectors = {
        		//article list
        		//article detail
                ".berita-terkait", ".terpopuler1"
                //common
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {
    			"(?i)^NERACA\\s*", 													//"NERACA"
    			"(?i)^Jakarta\\s*\\p{Pd}\\s*"										//"Jakarta - ", "Jakarta- ", "Jakarta -", "Jakarta-"
    			};
    	String[] SUFFIX = {
    			"(?i)\\(\\s*[^)]*\\s*\\)\\s*$"										//"(bani)"
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
