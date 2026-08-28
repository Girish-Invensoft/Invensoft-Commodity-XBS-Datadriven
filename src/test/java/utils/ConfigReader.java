package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final String CONFIG_FILE = "config/values.properties";
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        // Load from classpath instead of a relative file path.
        // This works no matter what the JVM's working directory is —
        // IDE run configs, mvn from any folder, CI runners, etc.
        try (InputStream inputStream =
                     ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "Could not find '" + CONFIG_FILE + "' on the classpath. " +
                                "Make sure it exists at src/test/resources/" + CONFIG_FILE +
                                " and that Maven copied it to target/test-classes."
                );
            }
            properties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file: " + CONFIG_FILE, e);
        }
    }

    /**
     * Returns the highlight wait duration in milliseconds.
     * Defaults to 500ms if not set in values.properties.
     */
    public static long getHighlightWaitMillis() {
        return Long.parseLong(getPropertyOrDefault("highlight.wait.millis", "500"));
    }


    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(
                    "Missing required property '" + key + "' in " + CONFIG_FILE +
                            ". Available keys: " + properties.stringPropertyNames()
            );
        }
        return value.trim();
    }

    /**
     * Returns a property with a fallback default if missing.
     */
    public static String getPropertyOrDefault(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }
}