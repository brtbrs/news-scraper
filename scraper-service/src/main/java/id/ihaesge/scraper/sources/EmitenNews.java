package id.ihaesge.scraper.sources;

import id.ihaesge.scraper.core.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

import com.microsoft.playwright.*;

public class EmitenNews extends BaseScraper implements NewsSource {
	private final String BASE_URL = "https://www.emitennews.com/";
	private final String EMITEN_URL = BASE_URL + "category/emiten/";
	private final String[] sitemap = {
//			"https://www.emitennews.com/sitemap-current-1.xml", 	//done
//			"https://www.emitennews.com/sitemap-current-2.xml", 	//done
//			"https://www.emitennews.com/sitemap-current-3.xml", 	//done
//			"https://www.emitennews.com/sitemap-current-4.xml", 	//done
//			"https://www.emitennews.com/sitemap-current-5.xml", 	//done
//			"https://www.emitennews.com/sitemap-2025.xml",			//done
//			"https://www.emitennews.com/sitemap-2024.xml",			//done
//			"https://www.emitennews.com/sitemap-2023.xml",			//done
//			"https://www.emitennews.com/sitemap-2022.xml",			//done
//			"https://www.emitennews.com/sitemap-2021.xml",			//done
//			"https://www.emitennews.com/sitemap-2020.xml",			//404
//			"https://www.emitennews.com/sitemap-2019.xml",			//404
//			"https://www.emitennews.com/sitemap-2018.xml",			//404
//			"https://www.emitennews.com/sitemap-2017.xml",			//404
	};

    @Override
    public String getSourceName() {
        return "EMITENNEWS";
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
        Document doc = Jsoup.connect(EMITEN_URL).get();

        List<Content> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        //<div class="main-layout" id="headerLayout">
        Element div = doc.selectFirst("div.main-layout");
        for (Element el : div.select("a")) {
            String href = el.attr("href");
//                String title = cleanText(el.text());

            //<a href="https://www.emitennews.com/news/aspr-kena-suspensi-kedua-potensi-dikunci-sepekan" class="news-card-2 search-result-item">
            if (href.contains("/news/")) {

            	if (!href.startsWith("http")) {
            		href = BASE_URL + href;
            	}

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
            //<div class="text-not-found">	404
            if (doc.selectFirst("div.text-not-found") == null) {
            	//<div class="breadcumb">
            	Element breadcumb = doc.selectFirst("div.breadcumb");
            	String category = breadcumb.text().trim();
            	if (category.contains("Emiten") || category.contains("Riset") || category.contains("Regulator") || category.contains("Informasi")) {
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
            	} else {
            		content = new Content("NOT EMITEN / RISET / REGULATOR / INFORMASI CATEGORY", url, "EMITENNEWS");
            	}
            } else {
            	System.out.println("404404404404404404404404404404");
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
//        	removeNoiseKontan(doc);

        	String title = cleanText(doc.selectFirst("title").text());
            LocalDateTime ldt = extractPublishDate(doc);

            StringBuilder sb = new StringBuilder();
            //<div class="article-body">
            Element div = doc.selectFirst("div.article-body");
            for (Element p : div.select("p")) {
            	String clean = cleanText(p.text());
                if (clean != null) {
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

    //<span class="time-posted">26/03/2026, 16:30 WIB</span>
    private LocalDateTime extractPublishDate(Document doc) {
        Element element = doc.selectFirst("span.time-posted");
        if (element != null) {
        	String[] parts = element.text().replaceAll("WIB", "").trim().split(",");
            if (parts.length == 2) {
            	String combine = parts[0].trim() + " " + parts[1].trim();
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("id", "ID"));
            	LocalDateTime ldt = LocalDateTime.parse(combine, formatter);
            	return ldt;
            }
        }

        return null;
    }

//    private void removeNoiseEmitenNews(Document doc) {
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
//    	String[] PREFIX = {"EmitenNews.com -", "EmitenNews.com - ", "EmitenNews.com- ", "EmitenNews.com-"};	//must in order
    	String[] PREFIX = {"(?i)^EmitenNews.com\\s*\\p{Pd}\\s*"};
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
