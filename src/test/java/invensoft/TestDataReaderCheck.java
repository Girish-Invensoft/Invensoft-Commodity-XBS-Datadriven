package invensoft;

import invensoft.testdata.ContractTestData;
import invensoft.testdata.ProductDetailRow;
import utils.TestDataReader;

public class TestDataReaderCheck {
    public static void main(String[] args) throws Exception {
        ContractTestData data = TestDataReader.getByTestCaseId("TC_CCM_001");
        System.out.println("Test Case ID: " + data.getTestCaseId());

            for (ProductDetailRow row : data.getProductDetails()) {
                System.out.println("Product: " + row.getProduct());
                System.out.println("Qty: " + row.getQty());
                System.out.println("Target Date Range: " + row.getTargetDateRange());
                System.out.println("Position Month: " + row.getPositionMonth());
                System.out.println("Certification & Premium: " + row.getCertificationPremium());
            }
        }
    }
