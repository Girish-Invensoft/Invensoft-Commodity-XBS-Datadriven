@XBS_Commodity_Contract_Management_Flow_PTBF
Feature: As a Contract Manager,I want to create a Commodity Contract by completing all available fields,
  so that I can validate the complete PTBF business workflow, field validations, business rules, and end-to-end contract processing in detail.

  @XBS_Commodity_Contract_Management_PTBF @RegressionTesting
  Scenario Outline: Create commodity contract management with PTBF flow

    ## ═════════════════════════════════════════════════════════════════════════
    ## 🔐  SETUP & LOGIN
    ## ═════════════════════════════════════════════════════════════════════════
    Given test data "<TestCaseId>" is loaded
    And open browser

    When enter the user name as "@login.username"
    When enter the user password as "@login.password"
    Then the user should be navigated to the dashboard

    ## ═════════════════════════════════════════════════════════════════════════
    ## 🧭  NAVIGATION
    ## ═════════════════════════════════════════════════════════════════════════
    When user navigates to "Commercial" dashboard sidebar menu
    When user clicks on "Commodity Contract Management" section on the dashboard page

    When clicks on "Create" "plus" icon on the Commodity Contract Management dashboard

    And verifies that the "General" section is displayed
    And verifies that the "Product Details" table section is displayed

    ## ═════════════════════════════════════════════════════════════════════════
    ## ⚠️  MANDATORY FIELD VALIDATION
    ## ═════════════════════════════════════════════════════════════════════════

    Then verify the mandatory validation indicator is displayed for fields "Class,Pricing"
    When user selects "@general.class" from the "Class" dropdown in the "General" section
    When user selects "@general.pricing" from the "Pricing" dropdown in the "General" section
    And click on "Save" button
    Then verify the mandatory validation indicator is displayed for fields "Class,Pricing,Billing Location,Shipping Location,Trader,Season,Counterparty,Exchange,Shipper,Fixation By,Fixation Terms,Invoice Currency,Price Currency / UOM,Tax Structure,Season,Product,Units,Packing,Differential,Trade Month,Qty.,UOM,Target Date Range,Position Month,INCO Terms,Abritration,Weight Terms"
    Then Verify step number "1" section "General" displays the mandatory red badge
    Then Verify step number "2" section "Product Details" displays the mandatory red badge
    Then Verify step number "4" section "Delivery Terms" displays the mandatory red badge
    ## ═════════════════════════════════════════════════════════════════════════
    ## 📝  CREATE  ›  GENERAL SECTION
    ## ═════════════════════════════════════════════════════════════════════════

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
    When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section

    When user selects "@commissionDetailsValidation.type1" verify "@general.invoiceCurrency" value is displayed

    When user selects "@general.changedExchange" from the "@general.exchange" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "@general.changedExchangePriceCurrencyUom"
    When user selects "@commissionDetailsValidation.type2" verify "@general.changedExchangePriceCurrencyUom" value is displayed

    When user selects "@general.fixationBy" from the "Fixation By" literal dropdown in the "General" section
    When user selects "@general.fixationTerms" from the "Fixation Terms" dropdown in the "General" section

#    When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section
    When user selects "@general.paymentTerms" from the "Payment Terms" dropdown in the "General" section
    When user enters "@general.paymentTermsDescription" in "Payment Terms Description" long text area field in the "General" section
    When user selects "@general.taxStructure" from the "Tax Structure" dropdown in the "General" section
    When user selects "@general.withHoldingTaxStructure" from the "With Holding Tax Structure" dropdown in the "General" section

    Then Verify step number "1" section "General" displays the green completed icon
    ## ═════════════════════════════════════════════════════════════════════════
    ## 📦  CREATE  ›  PRODUCT & COMMISSION DETAILS
    ## ═════════════════════════════════════════════════════════════════════════
    Given user fills multiple PTBF product details for test case "<TestCaseId>"
    Then Verify step number "2" section "Product Details" displays the green completed icon
    Given user fills multiple commission details for test case "<TestCaseId>"
    Then Verify step number "3" section "Commission Details" displays the green completed icon
    ## ═════════════════════════════════════════════════════════════════════════
    ## 🚢  CREATE  ›  DELIVERY TERMS
    ## ═════════════════════════════════════════════════════════════════════════
    When the user selects Incoterms "EXW,CIP,CPT,DAF,DDP,DDU,FCA" one by one and verifies "Place" is mandatory

    When user selects "@validatedeliveryTerms.portOfLoading" from the "Port Of Loading" dropdown in the "Delivery Terms" section
    When user selects "@validatedeliveryTerms.portOfDischarge" from the "Port Of Discharge" dropdown in the "Delivery Terms" section
    Then verify the validation message "Port Of Loading And Port Of Discharge Cannot Be Same" is displayed

    When user selects "@deliveryTerms.incoTerms" from the "INCO Terms" stale dropdown in the "Delivery Terms" section
    When user enters "@deliveryTerms.place" in "Place" text field in the "Delivery Terms" section
    When user selects "@deliveryTerms.basisOfSale" from the "Basis Of Sale" dropdown in the "Delivery Terms" section
    When user selects "@modify.portOfLoading" from the "Port Of Loading" modified dropdown in the "Delivery Terms" section
    When user enters "@deliveryTerms.polDescription" in "POL Description" text field in the "Delivery Terms" section

    When user selects "@deliveryTerms.portOfDischarge" from the "Port Of Discharge" modified dropdown in the "Delivery Terms" section
    When user enters "@deliveryTerms.podDescription" in "POD Description" text field in the "Delivery Terms" section
    When user selects "@deliveryTerms.finalDestination" from the "Final Destination" dropdown in the "Delivery Terms" section
    When user selects "@deliveryTerms.weightTerms" from the "Weight Terms" dropdown in the "Delivery Terms" section
    When user selects "@deliveryTerms.countryOfOrigin" from the "Country Of Origin" dropdown in the "Delivery Terms" section

    When user selects "@deliveryTerms.arbitration" from the "Abritration" dropdown in the "Delivery Terms" section
    When user enters "@deliveryTerms.termsAndConditions" in "Terms And Conditions" long text area field in the "Delivery Terms" section
    Then Verify step number "4" section "Delivery Terms" displays the green completed icon

    ## ═════════════════════════════════════════════════════════════════════════
    ## 📄  CREATE  ›  DOCUMENTS
    ## ═════════════════════════════════════════════════════════════════════════
    When user selects "@documents.documentSet" from the "Document Set" dropdown in the "Documents" section
    When the user selects the checkboxes listed in test data "documents.select" under "Document Set"

    When the user deselects the checkboxes listed in test data "documents.deselect" under "Document Set"

    ## ═════════════════════════════════════════════════════════════════════════
    ## 💬  CREATE  ›  REMARKS & INSTRUCTIONS
    ## ═════════════════════════════════════════════════════════════════════════
    When user enters "@remarks.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@remarks.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "@remarks.internalRemarks" in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    ## ═════════════════════════════════════════════════════════════════════════
    ## 💾  SAVE & VERIFY CREATED CONTRACT
    ## ═════════════════════════════════════════════════════════════════════════
    And click on "Save" button
    And verify the message "@contract.createdMessage" is displayed
    Then capture "CommercialCommodityPO" "Contract No."
    Then user captures the created contract number

    And verify "Contract No." is in "@contract.pendingStatus" status
    Then verify the "@contract.pendingMenuItems" menu items is displayed for "Contract No."
    Then search for the created "CommercialCommodityPO" by "ContractNumber"
    When the user selects the "Purchase""checkbox" for the created Purchase Order
    Then apply a static wait for the web element for testing purposes only.

    ## ═════════════════════════════════════════════════════════════════════════
    ## 🔄  MODIFY FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    And select the "@modify.menuItem" menu item for the "Contract No."

    When user selects "@modify.billingLocation" from the "Billing Location" modified dropdown in the "General" section
    When user selects "@modify.shippingLocation" from the "Shipping Location" modified dropdown in the "General" section
    When user selects "@modify.season" from the "Season" modified dropdown in the "General" section
    When user selects "@modify.class" from the "Class" modified dropdown in the "General" section

    When user selects "@modify.counterparty" from the "Counterparty" modified dropdown in the "General" section
    When user selects "@modify.shipper" from the "Shipper" modified dropdown in the "General" section
    When user selects "@amendment.fixationBy" from the "Fixation By" literal dropdown in the "General" section

    When user selects "@modify.invoiceCurrency" from the "Invoice Currency" modified dropdown in the "General" section

    When user selects "@modify.incoTerms" from the "INCO Terms" modified dropdown in the "Delivery Terms" section
    When user selects "@modify.portOfLoading" from the "Port Of Loading" modified dropdown in the "Delivery Terms" section
    When user selects "@modify.portOfDischarge" from the "Port Of Discharge" modified dropdown in the "Delivery Terms" section

    When user enters "@modify.termsAndConditions" in "Terms And Conditions" long text area field in the "Delivery Terms" section
    When user enters "@modify.polDescription" in "POL Description" text field in the "Delivery Terms" section

    When user selects "@modify.weightTerms" from the "Weight Terms" modified dropdown in the "Delivery Terms" section
    When user selects "@modify.arbitration" from the "Abritration" modified dropdown in the "Delivery Terms" section

    When user enters "@modify.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@modify.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "@modify.internalRemarks" in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    And click on "Update" button
    And verify the message "@modify.modifiedMessage" is displayed
    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  APPROVAL FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    Then verify "@approval.approveButton""@approval.approveIcon" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "@approval.approveButton" button on the Commercial Commodity Contract Management page

    Then verify "@approval.confirmationMessage" message is displayed with "@approval.yesButton" and "@approval.noButton" buttons
    Then click on "@approval.yesButton" button

#    And verify the message "@approval.approvedMessage" is displayed
    Then capture "ApprovedCommercialCommodityPO" "Contract No."

#    Then verify the "@approval.approvedMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@approval.approvedStatus" status

    ## ═════════════════════════════════════════════════════════════════════════
    ## 🔄  AMENDMENT FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    And select the "@amendment.menuItem" menu item for the "Contract No."

    Then verify that the "Class" field in "General" section with value "@amendment.class" is "disabled" and displays "@amendment.class"
    Then verify that the "Pricing" field in "General" section with value "@amendment.pricing" is "disabled" and displays "@amendment.pricing"
    Then verify that the "Amend No." field is "@amendment.fieldState".

    ## ── Amendment › General ──────────────────────────────────────────────────
    When user selects "@amendment.billingLocation" from the "Billing Location" modified dropdown in the "General" section
    When user selects "@amendment.shippingLocation" from the "Shipping Location" modified dropdown in the "General" section

    Then verify that the "Trader" field is automatically populated based on the logged-in "@login.loggedInUser"
    When user selects "@amendment.season" from the "Season" dropdown in the "General" section

    When user selects "@amendment.counterparty" from the "Counterparty" modified dropdown in the "General" section
    When user selects "@amendment.shipper" from the "Shipper" modified dropdown in the "General" section
    When user enters "@amendment.cpRef1" in "CP Ref 1" text field in the "General" section
    When user enters "@amendment.cpRef2" in "CP Ref 2" text field in the "General" section

    When user selects "@amendment.exchange" from the "Exchange" modified dropdown in the "General" section
    Then verify that the "amendment Currency / UOM" field is displayed as "@general.exchangePriceCurrencyUom"

    When user selects "@amendment.changedExchange" from the "@general.exchange" modified dropdown in the "General" section
    Then verify that the "amendment Currency / UOM" field is displayed as "@general.changedExchangePriceCurrencyUom"

    When user selects "@amendment.fixationBy" from the "Fixation By" literal dropdown in the "General" section
    When user selects "@amendment.fixationTerms" from the "Fixation Terms" modified dropdown in the "General" section

    When user selects "@amendment.invoiceCurrency" from the "Invoice Currency" modified dropdown in the "General" section
    When user selects "@amendment.paymentTerms" from the "Payment Terms" modified dropdown in the "General" section
    When user enters "@amendment.paymentTermsDescription" in "Payment Terms Description" long text area field in the "General" section
    When user selects "@amendment.taxStructure" from the "Tax Structure" modified dropdown in the "General" section
    When user selects "@amendment.withHoldingTaxStructure" from the "With Holding Tax Structure" modified dropdown in the "General" section

    ## ── Amendment › Product & Commission Details ─────────────────────────────
    Given user fills multiple PTBF product details for test case "<TestCaseId>"
    Given user fills multiple commission details for test case "<TestCaseId>"

    ## ── Amendment › Delivery Terms ───────────────────────────────────────────
    When user selects "@amendment.incoTerms" from the "INCO Terms" stale dropdown in the "Delivery Terms" section
    When user enters "@amendment.place" in "Place" text field in the "Delivery Terms" section
    When user selects "@amendment.basisOfSale" from the "Basis Of Sale" modified dropdown in the "Delivery Terms" section
    When user selects "@amendment.portOfLoading" from the "Port Of Loading" modified dropdown in the "Delivery Terms" section
    When user enters "@amendment.polDescription" in "POL Description" text field in the "Delivery Terms" section

    When user selects "@amendment.portOfDischarge" from the "Port Of Discharge" modified dropdown in the "Delivery Terms" section
    When user enters "@amendment.podDescription" in "POD Description" text field in the "Delivery Terms" section
    When user selects "@amendment.finalDestination" from the "Final Destination" modified dropdown in the "Delivery Terms" section
    When user selects "@amendment.weightTerms" from the "Weight Terms" modified dropdown in the "Delivery Terms" section
    When user selects "@amendment.countryOfOrigin" from the "Country Of Origin" modified dropdown in the "Delivery Terms" section

    When user selects "@amendment.arbitration" from the "Abritration" modified dropdown in the "Delivery Terms" section
    When user enters "@amendment.termsAndConditions" in "Terms And Conditions" long text area field in the "Delivery Terms" section

    ## ── Amendment › Documents ────────────────────────────────────────────────
    When user selects "@amendment.documentSet" from the "Document Set" modified dropdown in the "Documents" section
    When the user selects the checkboxes listed in test data "amendment.select" under "Document Set"

    When the user deselects the checkboxes listed in test data "amendment.deselect" under "Document Set"

    ## ── Amendment › Remarks & Instructions ───────────────────────────────────
    When user enters "@amendment.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@amendment.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "@amendment.internalRemarks" in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    ## ── Amendment › Submit & Verify ──────────────────────────────────────────
    And click on "Amend" button
    Then capture "CommercialCommodityAmendmentPO" "Contract No."

    Then verify the message "@contractAmendment.amendedMessage" is displayed

    And verify "Contract No." is in "@contractAmendment.pendingStatus" status
    Then verify the "@contractAmendment.pendingMenuItems" menu items is displayed for "Contract No."
    Then search for the created "CommercialCommodityPO" by "ContractNumber"

    When the user selects the "Purchase""checkbox" for the created Purchase Order
    Then apply a static wait for the web element for testing purposes only.

    ## ── Amendment › Approval ─────────────────────────────────────────────────
    Then verify "@approval.approveButton""@approval.approveIcon" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "@approval.approveButton" button on the Commercial Commodity Contract Management page

    Then verify "@approval.confirmationMessage" message is displayed with "@approval.yesButton" and "@approval.noButton" buttons
    Then click on "@approval.yesButton" button

    And verify the message "@approval.approvedMessage" is displayed
    Then capture "ApprovedCommercialCommodityAmmendmentPO" "Contract No."

    Then verify the "@approval.approvedMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@approval.approvedStatus" status

    ## ═════════════════════════════════════════════════════════════════════════
    ## 🔄  SECOND AMENDMENT FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    And select the "@amendment.menuItem" menu item for the "Contract No."

    Then verify that the "Class" field in "General" section with value "@amendment.class" is "disabled" and displays "@amendment.class"
    Then verify that the "Pricing" field in "General" section with value "@amendment.pricing" is "disabled" and displays "@amendment.pricing"
    Then verify that the "Amend No." field is "@amendment.fieldState".

    When user selects "@secondAmendment.billingLocation" from the "Billing Location" modified dropdown in the "General" section
    When user enters "@secondAmendment.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@secondAmendment.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "@secondAmendment.internalRemarks" in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    And click on "Amend" button
    Then capture "CommercialCommoditySecondAmendmentPO" "Contract No."

    ## ═════════════════════════════════════════════════════════════════════════
    ## ❌  CANCEL AMENDMENT FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    And select the "@contractAmendment.amendmentcancelMenuItem" menu item for the "Contract No."

    Then verify "@contractAmendment.cancelconfirmationMessage" message is displayed with "@contractAmendment.yesButton" and "@contractAmendment.noButton" buttons
    Then click on "@contractAmendment.yesButton" button

    Then verify "@contractAmendment.reasonconfirmationMessage" message is displayed with "@contractAmendment.confirmButton" and "" buttons

    When user enters "@contractAmendment.reasonForCancel" in "Cancel" long text area pop up
    Then click on "@contractAmendment.confirmButton" button
    And verify the message "@contractAmendment.canceledMessage" is displayed

    Then verify the "@contractAmendment.cancelledMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@contractAmendment.approvedStatus" status

    ## ═════════════════════════════════════════════════════════════════════════
    ## 📋  COPY BY CONTRACT FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    And select the "@copyByContract.menuItem" menu item for the "Contract No."

    When user selects "@copyByContract.billingLocation" from the "Billing Location" modified dropdown in the "General" section
    When user selects "@copyByContract.shippingLocation" from the "Shipping Location" modified dropdown in the "General" section
    When user selects "@copyByContract.season" from the "Season" modified dropdown in the "General" section
    When user selects "@copyByContract.class" from the "Class" modified dropdown in the "General" section

    When user selects "@copyByContract.counterparty" from the "Counterparty" modified dropdown in the "General" section
    When user selects "@copyByContract.shipper" from the "Shipper" modified dropdown in the "General" section
    When user selects "@copyByContract.fixationBy" from the "Fixation By" literal dropdown in the "General" section

    When user selects "@copyByContract.invoiceCurrency" from the "Invoice Currency" modified dropdown in the "General" section

    Given user fills multiple PTBF product details for test case "<TestCaseId>"
    Given user fills multiple commission details for test case "<TestCaseId>"

    When user selects "@copyByContract.incoTerms" from the "INCO Terms" modified dropdown in the "Delivery Terms" section
    When user selects "@copyByContract.portOfLoading" from the "Port Of Loading" modified dropdown in the "Delivery Terms" section
    When user selects "@copyByContract.portOfDischarge" from the "Port Of Discharge" modified dropdown in the "Delivery Terms" section

    When user enters "@copyByContract.termsAndConditions" in "Terms And Conditions" long text area field in the "Delivery Terms" section
    When user enters "@copyByContract.polDescription" in "POL Description" text field in the "Delivery Terms" section

    When user selects "@copyByContract.weightTerms" from the "Weight Terms" modified dropdown in the "Delivery Terms" section
    When user selects "@copyByContract.arbitration" from the "Abritration" modified dropdown in the "Delivery Terms" section

    When user enters "@copyByContract.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@copyByContract.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section
    When user enters "@copyByContract.internalRemarks" in "Internal Remarks" long text area field in the "Remarks and Instructions" section

    And click on "Save" button
    Then verify the message "@copyByContract.contractcreatedMessage" is displayed
    Then capture "CommercialCommoditycopyByContractPO" "Contract No."
    And Logout
#

    ## ═════════════════════════════════════════════════════════════════════════
    ##   TEST DATA  (one row per test case id in contractTestData.json)
    ## ═════════════════════════════════════════════════════════════════════════
    Examples:
      | TestCaseId | TestCaseDescription (No usage - only to outline each test case)                       |
      | TC_CCM_PTBF_001 | Commodity flow with 1 product and 100 commission details in Edge with admin profile   |
