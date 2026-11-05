package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class JsonDataWriter {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Object LOCK = new Object();

    private JsonDataWriter() {}

    /**
     * Writes value into: [testCaseId] -> [section] -> [field]
     * Example: TC_CCM_SI_001 -> "Contract Selection" -> "contractNo."
     */
    public static void captureValue(String filePath, String testCaseId,
                                    String section, String field, String value) {
        synchronized (LOCK) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new IllegalStateException("JSON file not found: " + file.getAbsolutePath());
                }

                JsonNode root = MAPPER.readTree(file);

                ObjectNode testCase = findTestCase(root, testCaseId);
                if (testCase == null) {
                    throw new IllegalStateException("testCaseId not found in JSON: " + testCaseId);
                }

                JsonNode existing = testCase.get(section);
                ObjectNode sectionNode = (existing != null && existing.isObject())
                        ? (ObjectNode) existing
                        : testCase.putObject(section);

                sectionNode.put(field, value);

                MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, root);
                System.out.println("JSON updated: " + testCaseId + " > " + section + " > " + field + " = " + value);

            } catch (IOException e) {
                throw new UncheckedIOException("Failed to update JSON: " + filePath, e);
            }
        }
    }

    // Finds the object whose "testCaseId" matches, at any depth (object or array root)
    private static ObjectNode findTestCase(JsonNode node, String testCaseId) {
        if (node.isObject()) {
            JsonNode id = node.get("testCaseId");
            if (id != null && testCaseId.equals(id.asText())) {
                return (ObjectNode) node;
            }
        }
        for (JsonNode child : node) {
            ObjectNode found = findTestCase(child, testCaseId);
            if (found != null) return found;
        }
        return null;
    }
}