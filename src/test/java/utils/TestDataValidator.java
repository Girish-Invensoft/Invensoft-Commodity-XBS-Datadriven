package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Startup validator for contractTestData.json.
 *
 * Every test case must contain all sections and keys present in the reference
 * test case (TC_CCM_001). It reports, for each test case:
 *   - missing sections / keys
 *   - wrong node type (object vs array vs text vs number vs boolean)
 *   - blank or null values where the reference has a value
 *   - rows inside arrays (product / commission details) missing keys
 *   - duplicate keys / duplicate test case ids in the JSON file
 * Extra keys in a test case are allowed. Values are allowed to differ.
 *
 * Optionally also checks that every TestCaseId used in the feature file's
 * Examples table exists in the JSON.
 *
 * JSON root: either { "TC_CCM_001": {...}, "TC_CCM_002": {...} }
 * or wrapped one level down, e.g. { "testCases": { "TC_CCM_001": {...} } }.
 * The wrapper is detected automatically.
 */
public final class TestDataValidator {

    public static final String REFERENCE_ID = "TC_CCM_001";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

    private TestDataValidator() {
    }

    /** Validates the JSON file; throws IllegalStateException listing every issue. */
    public static void validate(String jsonPath) {
        System.out.println(">>> TestDataValidator running on " + new File(jsonPath).getAbsolutePath());
        List<String> errors = collectErrors(jsonPath);
        if (!errors.isEmpty()) {
            throw new IllegalStateException(format(errors));
        }
        System.out.println("TestDataValidator: all test cases match " + REFERENCE_ID + " (" + jsonPath + ")");
    }

    /** Validates the JSON file and also checks the feature file's Examples ids. */
    public static void validate(String jsonPath, String featurePath) {
        System.out.println(">>> TestDataValidator running on " + new File(jsonPath).getAbsolutePath());
        List<String> errors = collectErrors(jsonPath);
        errors.addAll(collectFeatureErrors(jsonPath, featurePath));
        if (!errors.isEmpty()) {
            throw new IllegalStateException(format(errors));
        }
        System.out.println("TestDataValidator: test data and feature Examples are consistent");
    }

    public static List<String> collectErrors(String jsonPath) {
        List<String> errors = new ArrayList<>();
        JsonNode cases = loadCases(jsonPath, errors);
        if (cases == null) {
            return errors;
        }
        JsonNode reference = cases.get(REFERENCE_ID);
        if (reference == null) {
            errors.add("Reference test case " + REFERENCE_ID + " not found");
            return errors;
        }
        Iterator<Map.Entry<String, JsonNode>> it = cases.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> tc = it.next();
            if (!REFERENCE_ID.equals(tc.getKey())) {
                compare(reference, tc.getValue(), tc.getKey(), "", errors);
            }
        }
        return errors;
    }

    private static List<String> collectFeatureErrors(String jsonPath, String featurePath) {
        List<String> errors = new ArrayList<>();
        JsonNode cases = loadCases(jsonPath, new ArrayList<>());
        if (cases == null) {
            return errors;
        }
        try {
            String text = new String(Files.readAllBytes(Paths.get(featurePath)));
            Matcher m = Pattern.compile("^\\s*\\|\\s*(TC_[A-Za-z0-9_]+)\\s*\\|", Pattern.MULTILINE).matcher(text);
            while (m.find()) {
                if (!cases.has(m.group(1))) {
                    errors.add("Feature Examples uses " + m.group(1) + " but it is not in the JSON");
                }
            }
        } catch (IOException e) {
            errors.add("Cannot read feature file " + featurePath + ": " + e.getMessage());
        }
        return errors;
    }

    /** Returns the object that holds the test cases (handles an optional wrapper). */
    private static JsonNode loadCases(String jsonPath, List<String> errors) {
        JsonNode root;
        try {
            root = MAPPER.readTree(new File(jsonPath));
        } catch (IOException e) {
            errors.add("Cannot read/parse JSON file " + jsonPath + " (duplicate keys count as errors): " + e.getMessage());
            return null;
        }
        if (root == null || !root.isObject()) {
            errors.add("JSON root must be an object: " + jsonPath);
            return null;
        }
        if (root.has(REFERENCE_ID)) {
            return root;
        }
        Iterator<Map.Entry<String, JsonNode>> it = root.fields();
        while (it.hasNext()) {
            JsonNode child = it.next().getValue();
            if (child.isObject() && child.has(REFERENCE_ID)) {
                return child;
            }
        }
        return root; // reference not found: caller reports it
    }

    private static void compare(JsonNode ref, JsonNode actual, String tcId, String path, List<String> errors) {
        String where = path.isEmpty() ? "(test case)" : path;
        if (actual == null || actual.isMissingNode()) {
            errors.add(tcId + ": missing '" + where + "'");
            return;
        }
        if (ref.isObject()) {
            if (!actual.isObject()) {
                errors.add(tcId + ": '" + where + "' should be an object but is " + actual.getNodeType());
                return;
            }
            Iterator<Map.Entry<String, JsonNode>> fields = ref.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> f = fields.next();
                String childPath = path.isEmpty() ? f.getKey() : path + "." + f.getKey();
                JsonNode child = actual.get(f.getKey());
                if (child == null) {
                    errors.add(tcId + ": missing '" + childPath + "'");
                } else {
                    compare(f.getValue(), child, tcId, childPath, errors);
                }
            }
        } else if (ref.isArray()) {
            if (!actual.isArray()) {
                errors.add(tcId + ": '" + where + "' should be an array but is " + actual.getNodeType());
                return;
            }
            // Row counts may differ (e.g. 50 vs 100 commission rows), so compare each
            // row only against the key set of the reference's first row.
            if (ref.size() > 0 && ref.get(0).isObject()) {
                for (int i = 0; i < actual.size(); i++) {
                    compare(ref.get(0), actual.get(i), tcId, path + "[" + i + "]", errors);
                }
            }
        } else {
            // scalar: a text/number/boolean value must not become null, blank or another type
            if (actual.isNull() || (actual.isTextual() && actual.asText().trim().isEmpty())) {
                boolean refBlank = ref.isNull() || (ref.isTextual() && ref.asText().trim().isEmpty());
                if (!refBlank) {
                    errors.add(tcId + ": '" + where + "' is blank");
                }
            } else if (ref.getNodeType() != actual.getNodeType() && !ref.isNull()) {
                errors.add(tcId + ": '" + where + "' should be " + ref.getNodeType() + " but is " + actual.getNodeType());
            }
        }
    }

    private static String format(List<String> errors) {
        StringBuilder sb = new StringBuilder("Test data validation failed (")
                .append(errors.size()).append(" issue(s)) against ").append(REFERENCE_ID).append(":\n");
        errors.forEach(e -> sb.append("  - ").append(e).append('\n'));
        return sb.toString();
    }

    /** Quick manual check: run this class to print every issue without starting the suite. */
    public static void main(String[] args) {
        String json = args.length > 0 ? args[0] : "src/test/resources/testdata/contractTestData.json";
        List<String> errors = collectErrors(json);
        if (errors.isEmpty()) {
            System.out.println("OK: no issues found in " + json);
        } else {
            errors.forEach(System.out::println);
        }
    }
}

