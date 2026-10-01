package invensoft.testdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContractTestData {

    private String testCaseId;
    private List<ProductDetailRow> productDetails;
    private List<CommissionDetailRow> commissionDetails;

    // Login details for the scenario: url, username, password and the name expected in the Trader field.
    private Map<String, Object> login;

    // Free-form sections. Values are Strings, except list values such as documents.select.
    // Maps keep the JSON open for new fields without a new POJO per section.
    private Map<String, Object> general;
    private Map<String, Object> deliveryTerms;
    private Map<String, Object> documents;
    private Map<String, Object> remarks;

    // Expected results and labels for the create -> approve -> amend flow.
    private Map<String, Object> contract;   // after Save: created message, Pending status and menu items
    private Map<String, Object> approval;   // approve button/icon, confirmation dialog, approved message/status/menu items
    private Map<String, Object> amendment;  // amendment menu item and the expected state of the Class field

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public List<ProductDetailRow> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductDetailRow> productDetails) {
        this.productDetails = productDetails;
    }

    public List<CommissionDetailRow> getCommissionDetails() {
        return commissionDetails;
    }

    public void setCommissionDetails(List<CommissionDetailRow> commissionDetails) {
        this.commissionDetails = commissionDetails;
    }

    public Map<String, Object> getGeneral() {
        return general;
    }

    public void setGeneral(Map<String, Object> general) {
        this.general = general;
    }

    public Map<String, Object> getDeliveryTerms() {
        return deliveryTerms;
    }

    public void setDeliveryTerms(Map<String, Object> deliveryTerms) {
        this.deliveryTerms = deliveryTerms;
    }

    public Map<String, Object> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, Object> documents) {
        this.documents = documents;
    }

    public Map<String, Object> getRemarks() {
        return remarks;
    }

    public void setRemarks(Map<String, Object> remarks) {
        this.remarks = remarks;
    }

    public Map<String, Object> getContract() {
        return contract;
    }

    public void setContract(Map<String, Object> contract) {
        this.contract = contract;
    }

    public Map<String, Object> getApproval() {
        return approval;
    }

    public void setApproval(Map<String, Object> approval) {
        this.approval = approval;
    }

    public Map<String, Object> getLogin() {
        return login;
    }

    public void setLogin(Map<String, Object> login) {
        this.login = login;
    }

    public Map<String, Object> getAmendment() {
        return amendment;
    }

    public void setAmendment(Map<String, Object> amendment) {
        this.amendment = amendment;
    }

    /**
     * Looks up a single text value by "section.key", e.g. "general.class" or "deliveryTerms.incoTerms".
     * Fails with a clear message (listing the available keys) if the section or key is missing or blank.
     */
    public String getValue(String path) {
        Object value = lookup(path);
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            throw new IllegalStateException("Test data '" + testCaseId + "': '" + path
                    + "' is missing, blank or not a text value. Available in that section: " + keysOf(path));
        }
        return (String) value;
    }

    /**
     * Looks up a list of text values by "section.key", e.g. "documents.select".
     * A missing key returns an empty list, so an optional list can simply be left out of the JSON.
     */
    public List<String> getList(String path) {
        Object value = lookup(path);
        List<String> result = new ArrayList<>();
        if (value == null) {
            return result;
        }
        if (!(value instanceof List)) {
            throw new IllegalStateException("Test data '" + testCaseId + "': '" + path + "' is not a list.");
        }
        for (Object item : (List<?>) value) {
            result.add(String.valueOf(item));
        }
        return result;
    }

    private Object lookup(String path) {
        Map<String, Object> section = sectionOf(path);
        return section.get(keyOf(path));
    }

    private Map<String, Object> sectionOf(String path) {
        int dot = (path == null) ? -1 : path.indexOf('.');
        if (dot <= 0 || dot == path.length() - 1) {
            throw new IllegalArgumentException("Test data path must look like 'section.key' but was: " + path);
        }
        String name = path.substring(0, dot);
        Map<String, Object> section;
        switch (name) {
            case "login":         section = login;         break;
            case "general":       section = general;       break;
            case "deliveryTerms": section = deliveryTerms; break;
            case "documents":     section = documents;     break;
            case "remarks":       section = remarks;       break;
            case "contract":      section = contract;      break;
            case "approval":      section = approval;      break;
            case "amendment":     section = amendment;     break;
            default:
                throw new IllegalArgumentException("Unknown test data section '" + name + "' in '" + path
                        + "'. Use login, general, deliveryTerms, documents, remarks, contract, approval or amendment.");
        }
        if (section == null) {
            throw new IllegalStateException("Test data '" + testCaseId + "' has no '" + name + "' section.");
        }
        return section;
    }

    private String keyOf(String path) {
        return path.substring(path.indexOf('.') + 1);
    }

    private String keysOf(String path) {
        return String.valueOf(sectionOf(path).keySet());
    }
}