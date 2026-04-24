package id.ihaesge.tagger.app;

import id.ihaesge.tagger.engine.ContentTaggerEngine;
import id.ihaesge.tagger.repository.JdbcTaggingRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Main {
    public static void main(String[] args) {
        String source = readRequiredArg(args, "--source=");
        LocalDate fromDate = LocalDate.parse(readRequiredArg(args, "--from="));		//yyyy-mm-dd
        LocalDate toDate = LocalDate.parse(readRequiredArg(args, "--to="));			//yyyy-mm-dd

        String jdbcUrl = readConfig("TAGGER_DB_URL", "jdbc:postgresql://localhost:5432/newsdibi");
        String jdbcUser = readConfig("TAGGER_DB_USER", "postgres");
        String jdbcPassword = readConfig("TAGGER_DB_PASSWORD", "postgres");

        Instant from = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = toDate.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);

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
