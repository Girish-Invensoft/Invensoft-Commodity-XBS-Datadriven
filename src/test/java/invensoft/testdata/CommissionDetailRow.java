package invensoft.testdata;

public class CommissionDetailRow {

    private String agentBroker;   // "Commission Agent / Broker" dropdown
    private String refNo;         // "Ref. No." text field
    private String type;          // "Type" dropdown (e.g. Flat / Percentage)
    private String value;         // "Value" numeric field
    private String valueUnit;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueUnit() {
        return valueUnit;
    }

    public void setValueUnit(String valueUnit) {
        this.valueUnit = valueUnit;
    }
}

