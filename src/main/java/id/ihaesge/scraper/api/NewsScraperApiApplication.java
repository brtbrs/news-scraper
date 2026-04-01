package id.ihaesge.scraper.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "id.ihaesge.scraper")
public class NewsScraperApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsScraperApiApplication.class, args);
    }
}
