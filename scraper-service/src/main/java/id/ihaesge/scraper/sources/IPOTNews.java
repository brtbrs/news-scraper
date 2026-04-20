package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class IPOTNews extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.ipotnews.com/ipotnews/";

    @Override
    public String getSourceName() {
        return "IPOTNEWS";
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

    private List<String> getNewsListFromSiteMap(int scrapLimit) throws Exception {
    	List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();


        return urls;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // ✅ MULTIPLE containers
        for (Element div : doc.select("div.keeptogether")) {

            // ✅ MULTIPLE links inside each container
            for (Element el : div.select("a[href]")) {
            	String href = el.attr("href");
//                String title = cleanText(el.text());

            	//<a href="newsDetail.php?jdl=Laba_Bersih_SGRO_di_2025_Turun_51_9___Tapi_Kas_Meroket_245_5_&news_id=215777&group_news=IPOTNEWS&taging_subtype=SGRO&name=&search=y_general&q=Prime Agri Resources&halaman=1">
                if (href.contains("newsDetail.php")) {
                	if (!href.startsWith("http")) {
                		href = BASE_URL + href;
                	}

                	//identified group_news = IPOTNEWS / RESEARCHNEWS / ALLNEWS
                	if ((href.contains("group_news=IPOTNEWS") || href.contains("group_news=RESEARCHNEWS")) && (!href.contains("taging_subtype=MARKETOVERVIEW"))) {
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

            if (scrapLimit > 0 && urls.size() >= scrapLimit) break;
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
        	//no need to remove noise because extraction only on specific part (selectFirst)
            String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            //<article>
            Element article = doc.selectFirst("article");
            String[] brs = article.html().split("<br>");
            StringBuilder sb = new StringBuilder();

            for (int i=0; i<brs.length; i++) {
                if (!brs[i].contains("Sumber :")) {
                	String clean = cleanText(brs[i]);
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
        //<meta property="article:published_time" content="2026-03-27 14:52:59">
        Element meta = doc.selectFirst("meta[property=article:published_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"));
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

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {
    			"(?i)^JAKARTA\\s*,\\s*investor.id\\s*\\p{Pd}\\s*"					//yes, ipot also source its news from other channels
    			};
    	String[] SUFFIX = {
    			"(?i)\\(\\s*[^)]*\\s*\\)\\s*$"										//"(Budi/AI)", "(Dow Jones Newswires)", "(Bloomberg/AI)", "(reuters)"
    			};
    	str.trim();																	//be careful: \n at the end, dont forget to trim()

    	if (str != null && str.length() > 0) {
        	for (String s : PREFIX) {
    	    	if (str.startsWith(s)) {
    	    		str = str.substring(s.length()).trim();
//    	    		break;	//dont break because maybe have multiple prefixes
    	    	}
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
