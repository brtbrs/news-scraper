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
    public List<String> getNewsList(int scrapLimit, boolean fromSiteMap) throws Exception {
    	List<String> urls = new ArrayList<>();

    	if (fromSiteMap) {
    		urls = getNewsListFromSiteMap(scrapLimit);
    	} else {
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
            	if (href.contains("/finansial/bursa/") || href.contains("/finansial/korporasi/")) {
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
        Document docFinansial = Jsoup.connect(FINANSIAL_URL).get();

        List<String> urls = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="latest-news result">
        //<article class="article article--berita d-flex ">
        for (Element el : docFinansial.select("article.article.article--berita.d-flex")) {
            Element a = el.selectFirst("a[href]");
            String href = a.attr("href");

            //<a href="https://katadata.co.id/finansial/bursa/69dce5976e3b2/jelang-right-issue-entitas-happy-hapsoro-hilang-dari-daftar-pemilik-saham-padi">
            if (href.startsWith(FINANSIAL_URL) && (href.length() > FINANSIAL_URL.length())) {
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

        Document docKorporasi = Jsoup.connect(KORPORASI_URL).get();

        //<div class="latest-news result">
        //<article class="article article--berita d-flex ">
        for (Element el : docKorporasi.select("article.article.article--berita.d-flex")) {
            Element a = el.selectFirst("a[href]");
            String href = a.attr("href");

            //https://katadata.co.id/finansial/korporasi/69dc424e3faa5/manajemen-dewa-bicara-peluang-bagikan-dividen-usai-laba-melonjak">
            if (href.startsWith(KORPORASI_URL) && (href.length() > KORPORASI_URL.length())) {
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
    		e.printStackTrace();
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

    private void removeNoiseKataData(Document doc) {
        String[] selectors = {
                ".content-index-header", ".info-author", ".article-header-img", ".news-container.other-emiten-news-wrapper", ".recommendation-news-text"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    private String removePrefixSuffix(String str) {
    	//be careful: – is different -
    	String[] PREFIX = {};
    	String[] SUFFIX = {};
    	str.trim();																	//be careful: \n at the end, dont forget to trim()

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
