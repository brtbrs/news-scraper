package id.ihaesge.scraper.core;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class BaseScraper {
	protected String cleanText(String text) {
        return text
                .replaceAll("\\s+", " ")
                .replaceAll("\\n+", "\n")
                .replaceAll("<[^>]+>", "") 		// remove HTML tags
                .replaceAll("&quot;", "\"") 	// decode basic entities
                .trim();
    }

    protected void removeNoise(Document doc) {
        String[] selectors = {
                "link", "script", "style", "noscript", "iframe", "svg", "header", "footer",
                ".ads", ".advertisement", ".share", ".social", ".tags", ".related", ".comment", ".komentar", ".paging", ".pagination", ".banner"
        };

        for (String sel : selectors) {
            doc.select(sel).remove();
        }
    }

    protected String normalizeUrl(String url) {
//    	System.out.println("BEFORE: " + url);
    	String parsedUrl = null;
    	if (url != null) {
    		parsedUrl = Parser.unescapeEntities(url, false);
    	}
//    	System.out.println("AFTER: " + parsedUrl);
		return parsedUrl;
    }
}
