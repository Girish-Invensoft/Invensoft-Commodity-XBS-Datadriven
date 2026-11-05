package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import invensoft.testdata.ContractTestData;
import invensoft.testdata.NPEContractTestData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestDataReader {

    private static final String TEST_DATA_PATH =
            "src/test/resources/testdata/contractTestData.json";
    private static final String CONTRACT_ROOT_KEY = "contractTestData";
    private static final String NPE_ROOT_KEY = "NPEContractTestData";

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static JsonNode root;
    private static List<ContractTestData> contractCache;
    private static List<NPEContractTestData> npeCache;

    public static ContractTestData getByTestCaseId(String testCaseId) throws IOException {
        return findById(loadContractData(), testCaseId, ContractTestData::getTestCaseId);
    }

    public static NPEContractTestData getNpeByTestCaseId(String testCaseId) throws IOException {
        return findById(loadNpeData(), testCaseId, NPEContractTestData::getTestCaseId);
    }

    /**
     * Reads a dotted JSON path from the record matching testCaseId.
     * Example: contractShippingDetails.contractNo
     */
    public static synchronized String getValue(
            String testCaseId, String dottedPath) throws IOException {

        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException("testCaseId must not be blank");
        }
        if (dottedPath == null || dottedPath.isBlank()) {
            throw new IllegalArgumentException("dottedPath must not be blank");
        }

        JsonNode contractRecords = readRoot().get(CONTRACT_ROOT_KEY);
        if (contractRecords == null || !contractRecords.isArray()) {
            throw new IOException("Missing '" + CONTRACT_ROOT_KEY + "' array in "
                    + new File(TEST_DATA_PATH).getAbsolutePath());
        }

        JsonNode record = null;
        for (JsonNode candidate : contractRecords) {
            if (testCaseId.equals(candidate.path("testCaseId").asText())) {
                record = candidate;
                break;
            }
        }

        if (record == null) {
            throw new IllegalArgumentException(
                    "No contract test data found for testCaseId: " + testCaseId);
        }

        JsonNode valueNode = record;
        for (String part : dottedPath.split("\\.")) {
            valueNode = valueNode.path(part);
        }

        if (valueNode.isMissingNode() || valueNode.isNull()) {
            return null;
        }
        return valueNode.isValueNode() ? valueNode.asText() : valueNode.toString();
    }

    private static synchronized List<ContractTestData> loadContractData()
            throws IOException {
        if (contractCache == null) {
            contractCache = readSection(CONTRACT_ROOT_KEY, ContractTestData.class);
        }
        return contractCache;
    }

    private static synchronized List<NPEContractTestData> loadNpeData()
            throws IOException {
        if (npeCache == null) {
            npeCache = readSection(NPE_ROOT_KEY, NPEContractTestData.class);
        }
        return npeCache;
    }

    private static synchronized JsonNode readRoot() throws IOException {
        if (root == null) {
            File file = new File(TEST_DATA_PATH);
            if (!file.exists()) {
                throw new IOException(
                        "Test data file not found: " + file.getAbsolutePath());
            }
            root = mapper.readTree(file);
        }
        return root;
    }

    private static <T> List<T> readSection(String rootKey, Class<T> type)
            throws IOException {
        JsonNode section = readRoot().get(rootKey);
        if (section == null || !section.isArray() || section.isEmpty()) {
            throw new IOException("Missing or empty '" + rootKey + "' array in "
                    + new File(TEST_DATA_PATH).getAbsolutePath());
        }

        return mapper.convertValue(
                section,
                mapper.getTypeFactory()
                        .constructCollectionType(List.class, type));
    }

    private static <T> T findById(
            List<T> all, String testCaseId, Function<T, String> idOf) {
        return all.stream()
                .filter(td -> testCaseId != null
                        && testCaseId.equals(idOf.apply(td)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No test data found for testCaseId: " + testCaseId
                                + ". Available ids: "
                                + all.stream().map(idOf)
                                .collect(Collectors.toList())));
    }

    public static synchronized void reload() {
        root = null;
        contractCache = null;
        npeCache = null;
    }
}