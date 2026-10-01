package utils;
import java.util.HashMap;
import java.util.Map;

    public class TestContext {
        private static final Map<String, String> data = new HashMap<>();

        public static void set(String key, String value) {
            data.put(key, value);
        }

        public static String get(String key) {
            String value = data.get(key);
            if (value == null) {
                throw new RuntimeException("No runtime value stored for key: " + key);
            }
            return value;
        }

        public static boolean has(String key) {
            return data.containsKey(key);
        }

        public static void clear() {
            data.clear();
        }
    }

