package id.ihaesge.tagger.app;

import id.ihaesge.tagger.engine.ContentTaggerEngine;
import id.ihaesge.tagger.repository.JdbcTaggingRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Main {
    private static final String DEFAULT_TAGGER_TIMEZONE = "Asia/Jakarta";

    public static void main(String[] args) {
        String source = readRequiredArg(args, "--source=");
        LocalDate fromDate = LocalDate.parse(readRequiredArg(args, "--from="));		//yyyy-mm-dd
        LocalDate toDate = LocalDate.parse(readRequiredArg(args, "--to="));			//yyyy-mm-dd

        String jdbcUrl = readConfig("TAGGER_DB_URL", "");
        String jdbcUser = readConfig("TAGGER_DB_USER", "");
        String jdbcPassword = readConfig("TAGGER_DB_PASSWORD", "");
        ZoneId taggingZone = ZoneId.of(readConfig("TAGGER_TIMEZONE", DEFAULT_TAGGER_TIMEZONE));

        Instant from = fromDate.atStartOfDay(taggingZone).toInstant();
        Instant to = toDate.plusDays(1).atStartOfDay(taggingZone).minusNanos(1).toInstant();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)) {
            connection.setAutoCommit(true);
            ContentTaggerEngine engine = new ContentTaggerEngine(new JdbcTaggingRepository(connection));
            engine.run(source, from, to);
            System.exit(0);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to run tagger-service", e);
        }
    }

    private static String readConfig(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
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
