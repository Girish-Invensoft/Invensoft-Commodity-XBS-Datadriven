package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import invensoft.testdata.ContractTestData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestDataReader {

    private static final String TEST_DATA_PATH = "src/test/resources/testdata/contractTestData.json";
    private static final String ROOT_KEY = "contractTestData";
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Parsed once per run instead of once per step call.
    private static List<ContractTestData> cache;

    public static ContractTestData getByTestCaseId(String testCaseId) throws IOException {
        List<ContractTestData> all = loadAll();

        return all.stream()
                .filter(td -> testCaseId != null && testCaseId.equals(td.getTestCaseId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No test data found for testCaseId: " + testCaseId
                                + ". Available ids: "
                                + all.stream().map(ContractTestData::getTestCaseId).collect(Collectors.toList())));
    }

    private static synchronized List<ContractTestData> loadAll() throws IOException {
        if (cache == null) {
            File file = new File(TEST_DATA_PATH);
            if (!file.exists()) {
                throw new IOException("Test data file not found: " + file.getAbsolutePath());
            }

            Map<String, List<ContractTestData>> wrapper =
                    mapper.readValue(file, new TypeReference<Map<String, List<ContractTestData>>>() {});

            List<ContractTestData> list = (wrapper == null) ? null : wrapper.get(ROOT_KEY);
            if (list == null || list.isEmpty()) {
                throw new IOException("Missing or empty '" + ROOT_KEY + "' array in " + file.getPath());
            }
            cache = list;
        }
        return cache;
    }
}