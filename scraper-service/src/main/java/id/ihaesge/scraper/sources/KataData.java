package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.*;
import org.jsoup.nodes.*;

import com.microsoft.playwright.*;

public class KataData extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://katadata.co.id";
	private final String FINANSIAL_URL = BASE_URL + "/finansial/bursa";
	private final String KORPORASI_URL = BASE_URL + "/finansial/korporasi";
	private final String[] sitemap = {
//			"https://katadata.co.id/sitemap/post/2026-04.xml",		//done
//			"https://katadata.co.id/sitemap/post/2026-03.xml",		//done
//			"https://katadata.co.id/sitemap/post/2026-02.xml",		//done
//			"https://katadata.co.id/sitemap/post/2026-01.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-12.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-11.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-10.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-09.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-08.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-07.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-06.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-05.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-04.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-03.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-02.xml",		//done
//			"https://katadata.co.id/sitemap/post/2025-01.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-12.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-11.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-10.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-09.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-08.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-07.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-06.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-05.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-04.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-03.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-02.xml",		//done
//			"https://katadata.co.id/sitemap/post/2024-01.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-12.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-11.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-10.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-09.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-08.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-07.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-06.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-05.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-04.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-03.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-02.xml",		//done
//			"https://katadata.co.id/sitemap/post/2023-01.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-12.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-11.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-10.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-09.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-08.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-07.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-06.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-05.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-04.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-03.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-02.xml",		//done
//			"https://katadata.co.id/sitemap/post/2022-01.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-12.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-11.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-10.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-09.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-08.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-07.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-06.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-05.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-04.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-03.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-02.xml",		//done
//			"https://katadata.co.id/sitemap/post/2021-01.xml"		//done
			};

    @Override
    public String getSourceName() {
        return "KATADATA";
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
            	if (href.contains("/finansial/bursa/") || href.contains("/finansial/korporasi/")) {
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

        //<div class="latest-news">
        Element div = doc.selectFirst("div.latest-news");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//            String title = cleanText(el.text());

            //<a href="https://katadata.co.id/finansial/bursa/69cf770c1292f/bumn-karya-kian-mengkhawatirkan-rugi-ptpp-dan-wika-bengkak-berkali-kali-lipat">
            //https://katadata.co.id/finansial/korporasi/69d7aff228991/bank-ocbc-nisp-respons-dorongan-ojk-akui-tak-mudah-naik-kelas-ke-kbmi-4
            if (href.startsWith(FINANSIAL_URL) || href.startsWith(KORPORASI_URL)) {
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

        	content = extractContent(url, doc);

//            if (content == null) {
//                System.out.println("[" + getSourceName() + "] Playwright fallback: " + url);
//                Playwright pw = Playwright.create();
//                Browser browser = pw.chromium().launch(
//                		new BrowserType.LaunchOptions().setHeadless(true)
//                );
//
//                Page page = browser.newPage();
//                page.navigate(normalizeUrl(url));
//                page.waitForTimeout(2000);
//
//                doc = Jsoup.parse(page.content());
//                content = extractContent(url, doc);
//                page.close();
//                browser.close();
//            }
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

        	//<h1 class="detail-title mb-4">
        	String title = cleanText(doc.selectFirst("h1.detail-title.mb-4").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //<div class="detail-body mb-4">
            Element div = doc.selectFirst("div.detail-body.mb-4");
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

    //<meta property="article:published_time" content="2026-03-31 17:13:00">
    private LocalDateTime extractPublishDate(Document doc) {
        Element meta = doc.selectFirst("meta[property=article:published_time]");
        if (meta != null) {
            String publishDate = cleanText(meta.attr("content"));

        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", new Locale("id", "ID"));
        	LocalDateTime ldt = LocalDateTime.parse(publishDate, formatter);

        	return ldt;
        }

        return null;
    }

//    private void removeNoiseKataData(Document doc) {
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
    	String[] PREFIX = {};	//must in order
    	String[] SUFFIX = {};
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
