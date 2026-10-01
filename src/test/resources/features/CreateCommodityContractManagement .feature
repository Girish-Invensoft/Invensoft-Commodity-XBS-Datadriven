@XBS_Commodity_Contract_Management
Feature: Commodity Contract Management
  As a Contract Manager.I want to create a Commodity Contract with the mandatory fields
  So that I can quickly onboard a new supplier agreement without unnecessary data entry.

  @XBS_Commodity_Contract_Management_Flow_With_Approver
  Scenario Outline: Create commodity contract management with approver

    Given test data "<TestCaseId>" is loaded
    And open browser
    When enter the user name as "@login.username"
    When enter the user password as "@login.password"
    Then the user should be navigated to the dashboard

    When user navigates to "Commercial" dashboard sidebar menu
    When user clicks on "Commodity Contract Management" section on the dashboard page

    When clicks on "Create" "plus" icon on the Commodity Contract Management dashboard

    And verifies that the "General" section is displayed
    And verifies that the "Product Details" table section is displayed

    When user selects "@general.class" from the "Class" dropdown in the "General" section
    When user selects "@general.pricing" from the "Pricing" dropdown in the "General" section
    When user selects "@general.billingLocation" from the "Billing Location" dropdown in the "General" section
    When user selects "@general.shippingLocation" from the "Shipping Location" dropdown in the "General" section
    Then verify that the "Trader" field is automatically populated based on the logged-in "@login.loggedInUser"
    When user selects "@general.season" from the "Season" dropdown in the "General" section

    When user selects "@general.counterparty" from the "Counterparty" dropdown in the "General" section

    When user selects "@general.shipper" from the "Shipper" dropdown in the "General" section
    When user enters "@general.cpRef1" in "CP Ref 1" text field in the "General" section
    When user enters "@general.cpRef2" in "CP Ref 2" text field in the "General" section

    When user selects "@general.exchange" from the "Exchange" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "@general.exchangePriceCurrencyUom"
#
    When user selects "@general.changedExchange" from the "@general.exchange" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "@general.changedExchangePriceCurrencyUom"

    When user selects "@general.fixationBy" from the "Fixation By" literal dropdown in the "General" section
    When user selects "@general.fixationTerms" from the "Fixation Terms" dropdown in the "General" section

    When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section
    When user selects "@general.paymentTerms" from the "Payment Terms" dropdown in the "General" section
    When user enters "@general.paymentTermsDescription" in "Payment Terms Description" long text area field in the "General" section
    When user selects "@general.taxStructure" from the "Tax Structure" dropdown in the "General" section
    When user selects "@general.withHoldingTaxStructure" from the "With Holding Tax Structure" dropdown in the "General" section

    Given user fills multiple product details for test case "<TestCaseId>"
    Given user fills multiple commission details for test case "<TestCaseId>"

    When user selects "@deliveryTerms.incoTerms" from the "INCO Terms" stale dropdown in the "Delivery Terms" section
    When user selects "@deliveryTerms.portOfDischarge" from the "Port Of Discharge" dropdown in the "Delivery Terms" section
    When user selects "@deliveryTerms.weightTerms" from the "Weight Terms" dropdown in the "Delivery Terms" section
    When user enters "@deliveryTerms.place" in "Place" text field in the "Delivery Terms" section
    When user enters "@deliveryTerms.podDescription" in "POD Description" text field in the "Delivery Terms" section
    When user selects "@deliveryTerms.arbitration" from the "Abritration" dropdown in the "Delivery Terms" section
    When user selects "@deliveryTerms.basisOfSale" from the "Basis Of Sale" dropdown in the "Delivery Terms" section
    When user selects "@deliveryTerms.finalDestination" from the "Final Destination" dropdown in the "Delivery Terms" section

    When user selects "@deliveryTerms.portOfLoading" from the "Port Of Loading" dropdown in the "Delivery Terms" section
    When user enters "@deliveryTerms.termsAndConditions" in "Terms And Conditions" long text area field in the "Delivery Terms" section
    When user enters "@deliveryTerms.polDescription" in "POL Description" text field in the "Delivery Terms" section
    When user selects "@deliveryTerms.countryOfOrigin" from the "Country Of Origin" dropdown in the "Delivery Terms" section

    When user selects "@documents.documentSet" from the "Document Set" dropdown in the "Documents" section
    When the user selects the checkboxes listed in test data "documents.select" under "Document Set"

    When the user deselects the checkboxes listed in test data "documents.deselect" under "Document Set"

    When user enters "@remarks.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@remarks.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "@remarks.internalRemarks" in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    And click on "Save" button
    And verify the message "@contract.createdMessage" is displayed
    Then capture "CommercialCommodityPO" "Contract No."
    Then user captures the created contract number

    And verify "Contract No." is in "@contract.pendingStatus" status
    Then verify the "@contract.pendingMenuItems" menu items is displayed for "Contract No."
    Then search for the created "CommercialCommodityPO" by "ContractNumber"
    When the user selects the "checkbox" for the created Purchase Order

    Then verify "@approval.approveButton""@approval.approveIcon" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "@approval.approveButton" button on the Commercial Commodity Contract Management page

    Then verify "@approval.confirmationMessage" message is displayed with "@approval.yesButton" and "@approval.noButton" buttons
    Then click on "@approval.yesButton" button

    And verify the message "@approval.approvedMessage" is displayed
    Then capture "ApprovedCommercialCommodityPO" "Contract No."

    Then verify the "@approval.approvedMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@approval.approvedStatus" status

    And select the "@amendment.menuItem" menu item for the "Contract No."
    Then verify that the Class field is "@amendment.classFieldState" and displays the added "@general.class" value from the General section.
    And Logout

    Examples:
      | TestCaseId | TestCaseDescription (No usage - only to outline each test case)                    |
      | TC_CCM_001 | Commodity flow with 1 product and 1 commission detail in Edge with admin profile   |
      | TC_CCM_002 | Commodity flow with 5 products and 5 commission details in Chrome with admin profile |
      | TC_CCM_003 | Commodity flow with 1 product and 50 commission details in Firefox with admin profile |
      | TC_CCM_004 | Commodity flow with 1 product and 100 commission details in Edge with admin profile |
