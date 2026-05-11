
package id.ihaesge.scraper.sources;
import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class IQPlus extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.iqplus.info/news";
	private final String STOCK_NEWS_URL = BASE_URL + "/stock_news/";
	private final String[] sitemap = {
	};

    @Override
    public String getSourceName() {
        return "IQPlus";
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

    private List<String> getNewsListFromWebsiteOldNews(int startPage) throws Exception {
        List<String> urls = new ArrayList<>();

        //https://www.iqplus.info/news/stock_news/go-to-page,9.html					--> will call the php below
        //https://www.iqplus.info/box_listnews_more.php?csection=stock_news&id=9	--> then it will list non secure http news url
        //https://www.iqplus.info/news/stock_news/go-to-page,214.html				--> max 214 pages
    	for (int i=startPage; i<=startPage+13; i++) {
    		String page = "https://www.iqplus.info/box_listnews_more.php?csection=stock_news&id=" + i;
    		System.out.println("===== page : " + page);
    		Document doc = Jsoup.connect(page).get();

	        //<li style="text-transform:capitalize;">
	        for (Element el : doc.select("li[style*=\"text-transform:capitalize\"]")) {
	        	String href = el.selectFirst("a").attr("href");

	        	//<li style="text-transform:capitalize;"><b>05/05/26 - 02:41</b>
	        	//<a href="http://www.iqplus.info/news/stock_news/goto-rosan-ungkap-danantara-telah-masuk-investasi-di-saham-goto,12452858.html">GOTO: ROSAN UNGKAP DANANTARA TELAH MASUK INVESTASI DI SAHAM GOTO</a></li>
	            if (href.contains("www.iqplus.info/news/stock_news")) {		//http non secure
		        	if (!urls.contains(href)) {
	        			urls.add(href);
		        	}
	            }
	        }
    	}

        return urls;
    }

    private List<String> getNewsListFromWebsite(int scrapLimit) throws Exception {
        Document doc = Jsoup.connect(BASE_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<li style="text-transform:capitalize;">
        for (Element el : doc.select("li[style*=\"text-transform:capitalize\"]")) {
        	String href = el.selectFirst("a").attr("href");

            //<a href="https://www.iqplus.info/news/stock_news/psab-perkuat-kepemilikan--anak-usaha-psab-akuisisi-20--saham-jrbm,13058403.html">PERKUAT KEPEMILIKAN, ANAK USAHA PSAB AKUISISI 20% SAHAM JRBM</a> 
            if (href.contains(STOCK_NEWS_URL)) {
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

//        	<div id="zoomthis">
//  	  	  <div style="margin-top:10px;"><img src="/images/zoom.jpg" width="32" height="19" border="0" usemap="#Map">
//  	  		<map name="Map" id="Map">
//  	  		  <area shape="rect" coords="2,3,15,16" href="javascript:resizeText(1);" />
//  	  		  <area shape="rect" coords="16,4,29,16" href="javascript:resizeText(-1);" />
//  	  		</map>
//  	  	  </div>
//  		  <small>Monday 11/May/2026 at 16:14</small>
//  		  <h3>PERKUAT KEPEMILIKAN, ANAK USAHA PSAB AKUISISI 20% SAHAM JRBM.</h3>
//    		</div>
            Element div = doc.selectFirst("div#zoomthis");
            Element h3 = div.selectFirst("h3");

            String title = cleanText(h3.text());
            LocalDateTime ldt = extractPublishDate(doc);

            //<div id="zoomthis">
            StringBuilder sb = new StringBuilder();
            for (Node node = h3.nextSibling(); node != null; node = node.nextSibling()) {
                if (node instanceof TextNode) {
                	String clean = cleanText(((TextNode) node).text());
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
  		Element div = doc.selectFirst("div#zoomthis > small");
        if (div != null) {
        	String[] arr = div.text().split(" at ");
            String publishDate = cleanText(arr[0].split(" ")[1]);
            String publishTime = cleanText(arr[1]);

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm", new Locale("en", "EN"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate + " " + publishTime, formatter);

        	return ldt;
        }

        return null;
    }

    private void removeNoiseIQPlus(Document doc) {
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
    			"(?i)^IQPlus,\\s*\\(\\s*\\d{1,2}/\\d{1,2}\\s*\\)\\s*\\p{Pd}\\s*"	//IQPlus, (11/5) - 
    			};
    	String[] SUFFIX = {
    			"(?i)\\(\\s*[^)]*\\s*\\)\\s*$"										//"(end)", "(end/ant)"
    			};
    	str.trim();																	//be careful: \n at the end, dont forget to trim()

    	if (str != null && str.length() > 0) {
        	for (String s : PREFIX) {
        		str = str.replaceFirst(s, "").trim();
        	}

        	for (String s : SUFFIX) {
        		str = str.replaceFirst(s, "").trim();
        	}
    	}

    	return str;
    }
}
