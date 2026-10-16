@XBS_Commodity_Contract_Management
Feature: Commodity Contract Management
  As a Contract Manager.I want to create a Commodity Contract with the mandatory fields
  So that I can quickly onboard a new supplier agreement without unnecessary data entry.

  @XBS_Commodity_Contract_Management_Flow_Verify @smoke @regression
  Scenario: Create Commodity Contract Management end to end flow with only mandatory fields.

    ## Login
    Given open browser
    When enter the user name as "username"
    When enter the user password as "password"
    Then the user should be navigated to the dashboard

    ## Dashboard Verification
    And verify "Purchase" section is displayed on the dashboard page
    And verify "Sales" section is displayed on the dashboard page
    And verify "Inventory" section is displayed on the dashboard page
    And verify "WI" section is displayed on the dashboard page
    And verify "Product Summary" section is displayed on the dashboard page

    ## Dashboard sidebar menu
    Then verify "Commercial" is displayed in the dashboard sidebar menu
    Then verify "Logistics" is displayed in the dashboard sidebar menu
    Then verify "Quality" is displayed in the dashboard sidebar menu
    Then verify "Finance" is displayed in the dashboard sidebar menu
    Then verify "Production" is displayed in the dashboard sidebar menu
    Then verify "Setup" is displayed in the dashboard sidebar menu
    Then verify "Admin" is displayed in the dashboard sidebar menu
    Then verify "Futures" is displayed in the dashboard sidebar menu
    Then verify "reports" is displayed in the dashboard sidebar menu

    ## Navigate to Module
    When user navigates to "Commercial" dashboard sidebar menu
    And verify "Commodity Contract Management" section is displayed on the dashboard page
    When user clicks on "Commodity Contract Management" section on the dashboard page

    ## Verify buttons from commodity contract management dashboard
    Then verify "Create""plus" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Copy By Template""copy" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "NPE Conversion""currency_exchange" prime icon button is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Rolling""calendar" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Force Close""times" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Toggle Columns""bi bi-list-check" boot strap icon is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Filter""filter" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "More""angle-down" prime icons is displayed on the Commercial Commodity Contract Management dashboard

    ## Verify dashboard list view
    And verify "Type" field is displaying in list view
    And verify "Contract No." field is displaying in list view
    And verify "Contract Date" field is displaying in list view
    And verify "Pricing" field is displaying in list view
    And verify "Class" field is displaying in list view
    And verify "Counterparty" field is displaying in list view
    And verify "Status" field is displaying in list view

    ## Commodity Contract Management
    When clicks on "Create" "plus" icon on the Commodity Contract Management dashboard
    Then verifies that the "Commodity Contract Management - Create General" popup is displayed

    And verifies that the "General" section is displayed
    And verifies that the "Product Details" table section is displayed
    And verifies that the "Commission Details" table section is displayed
    And verifies that the "Delivery Terms" section is displayed
    And verifies that the "Documents" section is displayed
    And verifies that the "Remarks and Instructions" section is displayed

    And click on "Save" button
    Then verify the mandatory validation indicator is displayed for fields "Class,Pricing,Billing Location,Shipping Location,Trader,Season,Counterparty,Shipper,Invoice Currency,Price Currency / UOM,Tax Structure,Season,Product,Units,Packing,Qty.,UOM,Price,Target Date Range,Position Month,INCO Terms,Abritration,Weight Terms"

    When user selects "Coffee Farmer Purchase Contract" from the "Class" dropdown in the "General" section
    When user selects "EUR" from the "Invoice Currency" dropdown in the "General" section
    When user selects "INR/Kg" from the "Price Currency / UOM" dropdown in the "General" section
    When user selects "IMMEDIATE" from the "Payment Terms" dropdown in the "General" section

    When user selects "FLAT PRICE" from the "Pricing" dropdown in the "General" section
    When user selects "ABC Coffee Supplier (No. 77)" from the "Counterparty" dropdown in the "General" section
    When user selects "Default (DND)" from the "Billing Location" dropdown in the "General" section
    When user selects "Default (DND)" from the "Shipping Location" dropdown in the "General" section
    When user selects "ABC Coffee Supplier (No. 77)" from the "Shipper" dropdown in the "General" section
    When user selects "GST 18%" from the "Tax Structure" dropdown in the "General" section

    When user selects "Season 2026 - 2027" from the "Season" dropdown in the "General" section
    When user selects "Prior to 1st Notice Day" from the "Fixation Terms" dropdown in the "General" section
    When user selects "NEW YORK - ARABICA" from the "Exchange" dropdown in the "General" section

    And verifies that the "Product" column is displayed in the "Product Details" section table
    And verifies that the "Units" column is displayed in the "Product Details" section table
    And verifies that the "Packing" column is displayed in the "Product Details" section table
    And verifies that the "Qty." column is displayed in the "Product Details" section table
    And verifies that the "UOM" column is displayed in the "Product Details" section table
    And verifies that the "No Of Lots." column is displayed in the "Product Details" section table
    And verifies that the "Price" column is displayed in the "Product Details" section table
    And verifies that the "Target Type" column is displayed in the "Product Details" section table
    And verifies that the "Target Date Range" column is displayed in the "Product Details" section table
    And verifies that the "Trade Month" column is displayed in the "Product Details" section table
    And verifies that the "Position Month" column is displayed in the "Product Details" section table
    And verifies that the "Quality" column is displayed in the "Product Details" section table
    And verifies that the "Certification & Premium" column is displayed in the "Product Details" section table
    And verifies that the "Action" column is displayed in the "Product Details" section table

    Given user fills multiple product details for test case "TC_CCM_001"
    Given user fills multiple commission details for test case "TC_CCM_002"

    ###Adding data in Delivery Terms section
    When user selects "DDP" from the "INCO Terms" dropdown in the "Delivery Terms" section
    When user selects "Net Delivered Weights" from the "Weight Terms" dropdown in the "Delivery Terms" section
    When user enters "Mysuru" in "Place" text field in the "Delivery Terms" section
    When user selects "AB" from the "Abritration" dropdown in the "Delivery Terms" section

    When user selects "CERTIFICATES REQUIRED FOR ECC" from the "Document Set" dropdown in the "Documents" section
    When the user selects the "PHYTOSANITARY CERTIFICATE" checkbox under "Document Set"

    When user enters "Delivery should meet the agreed quality specifications." in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "Price confirmed with supplier; pending manager approval." in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "Pack in 1 MT Big Bags and deliver to the designated warehouse." in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    And click on "Save" button
    Then capture "Commercial Commodity" "Contract No."


  @XBS_Commodity_Contract_Management_Flow_With_Approver_Test
  Scenario Outline: Create commodity contract management with approver

    Given open browser
    When enter the user name as "<username>"
    When enter the user password as "<password>"
    Then the user should be navigated to the dashboard

    When user navigates to "Commercial" dashboard sidebar menu
    When user clicks on "Commodity Contract Management" section on the dashboard page

    When clicks on "Create" "plus" icon on the Commodity Contract Management dashboard

    And verifies that the "<General>" section is displayed
    And verifies that the "<ProductDetails>" table section is displayed

    And click on "Save" button

    When user selects "<class>" from the "Class" dropdown in the "General" section
    When user selects "<pricing>" from the "Pricing" dropdown in the "General" section
    When user selects "<billingLocation>" from the "Billing Location" dropdown in the "General" section
    When user selects "<shippingLocation>" from the "Shipping Location" dropdown in the "General" section
    Then verify that the "Trader" field is automatically populated based on the logged-in "<LogginUser>"
    When user selects "<season>" from the "Season" dropdown in the "General" section

    When user selects "<counterparty>" from the "Counterparty" dropdown in the "General" section

#    Then verify that the "Contract No." field displays "Auto".
#    And verify that the "Contract Date" field is displayed as "today's" date by default.

    When user selects "<shipper>" from the "Shipper" dropdown in the "General" section
    When user enters "<CPRef1>" in "CP Ref 1" text field in the "General" section
    When user enters "<CPRef2>" in "CP Ref 2" text field in the "General" section

    When user selects "LONDON - ROBUSTA" from the "Exchange" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "USD/MT"

    When user selects "NEW YORK - ARABICA" from the "LONDON - ROBUSTA" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "USC/LB"

    When user selects "<fixationcall>" from the "Fixation By" literal dropdown in the "General" section
    When user selects "<fixationTerms>" from the "Fixation Terms" dropdown in the "General" section

    When user selects "<invoiceCurrency>" from the "Invoice Currency" dropdown in the "General" section
    When user selects "<paymentTerms>" from the "Payment Terms" dropdown in the "General" section
    When user enters "---Advance payment: 100% payment required before order processing or service commencement." in "Payment Terms Description" long text area field in the "General" section
    When user selects "<taxStructure>" from the "Tax Structure" dropdown in the "General" section
    When user selects "<withholdingtaxstructure>" from the "With Holding Tax Structure" dropdown in the "General" section

    Given user fills multiple product details for test case "TC_CCM_001"

    ####Note:Commission details - Type flat price and value -currency not added as per selection-issue
    #Percentage and Flat price save not working --Blocker..##########
    Given user fills multiple commission details for test case "TC_CCM_001"

    When user selects "<incoTerms>" from the "INCO Terms" dropdown in the "Delivery Terms" section
    When user selects "<PortOfDischarge>" from the "Port Of Discharge" dropdown in the "Delivery Terms" section
    When user selects "<WeightTerms>" from the "Weight Terms" dropdown in the "Delivery Terms" section
    When user enters "<Place>" in "Place" text field in the "Delivery Terms" section
    When user enters "<poddescription>" in "POD Description" text field in the "Delivery Terms" section
    When user selects "<Abritration>" from the "Abritration" dropdown in the "Delivery Terms" section
    When user selects "<basicsofsale>" from the "Basis Of Sale" dropdown in the "Delivery Terms" section
    When user selects "<finaldestination>" from the "Final Destination" dropdown in the "Delivery Terms" section
    #When user selects "Busan" from the "Port Of Loading" dropdown in the "Delivery Terms" section
    #Then verify the validation message "Port of Loading and Port of Discharge cannot be the same" is displayed
    When user selects "<portofloading>" from the "Port Of Loading" dropdown in the "Delivery Terms" section
    When user enters "The goods must meet the agreed quality." in "Terms And Conditions" long text area field in the "Delivery Terms" section
    When user enters "<poldescription>" in "POL Description" text field in the "Delivery Terms" section
    When user selects "<countryoforigin>" from the "Country Of Origin" dropdown in the "Delivery Terms" section

    When user selects "<documentSet>" from the "Document Set" dropdown in the "Documents" section
    When the user selects the "PREFERENTIAL ENTRY CERTIFICATE" checkbox under "Document Set"
    When the user selects the "INSURANCE CERTIFICATE" checkbox under "Document Set"
    When the user selects the "WEIGHT AND QUALITY CERTIFICATE" checkbox under "Document Set"
    When the user selects the "HEALTH PHYTOSANITARY AND NON-RADIATION CERTIFICATES" checkbox under "Document Set"
    When the user selects the "FUMIGATION CERTIFICATE" checkbox under "Document Set"
    When the user selects the "3/3 ORIGINAL BILL OF LADING AND COPY OF NON-NEGOTIABLE" checkbox under "Document Set"
    When the user selects the "PHYTOSANITARY CERTIFICATE" checkbox under "Document Set"
    When the user selects the "NON-RADIATION CERTIFICATE" checkbox under "Document Set"
    When the user selects the "CERTIFICATE OF ORIGIN" checkbox under "Document Set"

    When the user deselects the "WEIGHT AND QUALITY CERTIFICATE" checkbox under "Document Set"
    When the user deselects the "HEALTH PHYTOSANITARY AND NON-RADIATION CERTIFICATES" checkbox under "Document Set"
    When the user deselects the "FUMIGATION CERTIFICATE" checkbox under "Document Set"

    When user enters "Delivery should meet the agreed quality specifications." in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "Price confirmed with supplier; pending manager approval." in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "Pack in 1 MT Big Bags and deliver to the designated warehouse." in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    And click on "Save" button
    And verify the message "CONTRACT CREATED SUCCESSFULLY." is displayed
    Then capture "CommercialCommodityPO" "Contract No."
    Then user captures the created contract number

    And verify "Contract No." is in "Pending" status
    Then verify the "Modify,Copy By Contract,Cancel" menu items is displayed for "Contract No."
    Then search for the created "CommercialCommodityPO" by "ContractNumber"
    When the user selects the "checkbox" for the created Purchase Order

    Then verify "Approve""check" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "Approve" button on the Commercial Commodity Contract Management page

    Then verify "Are you sure that you want to proceed ?" message is displayed with "Yes" and "No" buttons
    Then click on "Yes" button

    And verify the message "CONTRACT APPROVED SUCCESSFULLY" is displayed
    Then capture "ApprovedCommercialCommodityPO" "Contract No."

    Then verify the "Copy By Contract,Amendment" menu items is displayed for "Contract No."
    And verify "Contract No." is in "Approved" status

    And select the "Amendment" menu item for the "Contract No."
    Then verify that the Class field is "disabled" and displays the added "<class>" value from the General section.
    And Logout

    Examples:
      | username | password | class                           | invoiceCurrency | paymentTerms     | product | units | packing       | quantity | price | startDate | endDate | incoTerms | WeightTerms           | Place  | Abritration | documentSet                   | documentCertificate       | pricing    | counterparty                 | billingLocation | shippingLocation | shipper                      | taxStructure | season             | fixationTerms           | fixationcall  | exchange           | General | ProductDetails  | LogginUser | CPRef1         | C PRef2     | withholdingtaxstructure | PortOfDischarge | poddescription                       | basicsofsale | finaldestination | portofloading | poldescription                    | countryoforigin |
      | username | password | Coffee Farmer Purchase Contract | EUR             | IMMEDIATE        | AC A    | 3656  | 1 MT Big Bags | 600      | 600   | today     | today+3 | DDP       | Re Weights            | Uganda | AB          | CERTIFICATES REQUIRED FOR ECC | PHYTOSANITARY CERTIFICATE | FLAT PRICE | ABC Coffee Supplier (No. 77) | Default (DND)   | Default (DND)    | ABC Coffee Supplier (No. 77) | GST 18%      | Season 2026 - 2027 | Prior to 1st Notice Day | SELLER'S CALL | NEW YORK - ARABICA | General | Product Details | Girish     | SUP-2026-00125 | PO-ABC-4587 | TAX WHT1                | Hamburg         | Nhava Sheva Port, Maharashtra, India | SAS - Repeat | Busan            | Busan         | Port of Santos, Sao Paulo, Brazil | China           |
      | username | password | Coffee Vendor Purchase Contract | INR             | Purchase Advance | AC A    | 3656  | 1 MT Big Bags | 600      | 600   | today     | today+3 | DWH       | Net Delivered Weights | Mysuru | DB          | CERTIFICATES REQUIRED FOR ECC | PHYTOSANITARY CERTIFICATE | FLAT PRICE | LEMO (LEMO)                  | Default (DND)   | Default (DND)    | LEMO (LEMO)                  | GST 18%      | Season 2026 - 2027 | Prior to 1st Notice Day | SELLER'S CALL | NEW YORK - ARABICA | General | Product Details | Girish     | SUP-2026-00125 | PO-ABC-4587 | TAX WHT1                | Hamburg         | Nhava Sheva Port, Maharashtra, India | SAS - Repeat | Busan            | Busan         | Port of Santos, Sao Paulo, Brazil | China           |


