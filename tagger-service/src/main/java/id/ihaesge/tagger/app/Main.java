package id.ihaesge.tagger.app;

import id.ihaesge.tagger.engine.ContentTaggerEngine;
import id.ihaesge.tagger.repository.JdbcTaggingRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String DEFAULT_TAGGER_TIMEZONE = "Asia/Jakarta";
    private static final Map<String, String> DOTENV = loadDotenv();

    public static void main(String[] args) {
        String source = readRequiredArg(args, "--source=");
        LocalDate fromDate = LocalDate.parse(readRequiredArg(args, "--from="));		//yyyy-mm-dd
        LocalDate toDate = LocalDate.parse(readRequiredArg(args, "--to="));			//yyyy-mm-dd

        String jdbcUrl = readConfig("POSTGRES_JDBC_DB_URL", "");
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
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String dotenvValue = DOTENV.get(key);
        if (dotenvValue != null && !dotenvValue.isBlank()) {
            return dotenvValue;
        }

        return defaultValue;
    }

    private static Map<String, String> loadDotenv() {
        Map<String, String> values = new HashMap<>();
        loadDotenvFile(values, Path.of(".env"));
        loadDotenvFile(values, Path.of("..", ".env"));
        return values;
    }

    private static void loadDotenvFile(Map<String, String> values, Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                    continue;
                }

                int separator = trimmed.indexOf('=');
                String key = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    values.putIfAbsent(key, stripWrappingQuotes(value));
                }
            }
        } catch (Exception ignored) {
            // Optional file, ignore parse/read errors.
        }
    }

    private static String stripWrappingQuotes(String value) {
        if (value.length() < 2) {
            return value;
        }

        boolean wrappedWithDoubleQuotes = value.startsWith("\"") && value.endsWith("\"");
        boolean wrappedWithSingleQuotes = value.startsWith("'") && value.endsWith("'");
        if (wrappedWithDoubleQuotes || wrappedWithSingleQuotes) {
            return value.substring(1, value.length() - 1);
        }
        return value;
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
