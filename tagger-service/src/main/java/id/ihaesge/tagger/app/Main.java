package id.ihaesge.tagger.app;

import id.ihaesge.tagger.engine.ContentTaggerEngine;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Main {
    public static void main(String[] args) {
        String source = readRequiredArg(args, "--source=");
        LocalDate fromDate = LocalDate.parse(readRequiredArg(args, "--from="));		//yyyy-mm-dd
        LocalDate toDate = LocalDate.parse(readRequiredArg(args, "--to="));			//yyyy-mm-dd

        String apiBaseUrl = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080/api");

        Instant from = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = toDate.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);

        ContentTaggerEngine engine = new ContentTaggerEngine(apiBaseUrl);
        engine.run(source, from, to);
        System.exit(0);
    }

    private static String readRequiredArg(String[] args, String prefix) {
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("Missing argument: " + prefix + "...");
    }
}
