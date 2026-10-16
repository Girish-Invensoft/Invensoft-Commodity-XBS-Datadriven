package invensoft.testdata;

/**
  One row of "commissionDetailsValidation" in the test data JSON.
 */
public class CommissionDetailValidationRow {

    private String agentBroker;
    private String refNo;
    private String type1;
    private String type2;
    private String value;

    public String getAgentBroker() {
        return agentBroker;
    }

    public void setAgentBroker(String agentBroker) {
        this.agentBroker = agentBroker;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}