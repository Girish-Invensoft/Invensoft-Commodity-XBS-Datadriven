@XBS_Commodity_Contract_Management_Goods_Receipt_Note_Flow
Feature: Goods Receipt Note Flow in Commodity Contract Management

  @XBS_Commodity_Contract_Management_With_GRN_Purchase_Order@RegressionTesting
  Scenario Outline: Create commodity contract management and verify Goods Receipt Note.
    ## ═════════════════════════════════════════════════════════════════════════
    ## 🔐  SETUP & LOGIN
    ## ═════════════════════════════════════════════════════════════════════════
    Given test data "<TestCaseId>" is loaded
    And open browser

    When enter the user name as "@login.username"
    When enter the user password as "@login.password"
    Then the user should be navigated to the dashboard
#
    ## ═════════════════════════════════════════════════════════════════════════
    ## 🧭  NAVIGATION
    ## ═════════════════════════════════════════════════════════════════════════
    When user navigates to "Commercial" dashboard sidebar menu
    When user clicks on "Commodity Contract Management" section on the dashboard page

    When clicks on "Create" "create-new" icon on the Commodity Contract Management dashboard

    And verifies that the "General" section is displayed
    And verifies that the "Product Details" table section is displayed

    And click on "Save" button
    Then Verify step number "1" section "General" displays the mandatory red badge
    Then Verify step number "2" section "Product Details" displays the mandatory red badge
    Then Verify step number "4" section "Delivery Terms" displays the mandatory red badge
    ## ═════════════════════════════════════════════════════════════════════════
    ## 📝  CREATE  ›  GENERAL SECTION
    ## ═════════════════════════════════════════════════════════════════════════
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
    When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section

    When user selects "@commissionDetailsValidation.type1" verify "@general.invoiceCurrency" value is displayed

    When user selects "@general.changedExchange" from the "@general.exchange" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "@general.changedExchangePriceCurrencyUom"

    When user selects "@commissionDetailsValidation.type2" verify "@general.changedExchangePriceCurrencyUom" value is displayed

    When user selects "@general.fixationBy" from the "Fixation By" literal dropdown in the "General" section
    When user selects "@general.fixationTerms" from the "Fixation Terms" dropdown in the "General" section

    ###When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section
    When user selects "@general.paymentTerms" from the "Payment Terms" dropdown in the "General" section
    When user enters "@general.paymentTermsDescription" in "Payment Terms Description" long text area field in the "General" section
    When user selects "@general.taxStructure" from the "Tax Structure" dropdown in the "General" section
    When user selects "@general.withHoldingTaxStructure" from the "With Holding Tax Structure" dropdown in the "General" section

    Then Verify step number "1" section "General" displays the green completed icon
    ## ═════════════════════════════════════════════════════════════════════════
    ## 📦  CREATE  ›  PRODUCT & COMMISSION DETAILS
    ## ═════════════════════════════════════════════════════════════════════════
    Given user fills multiple product details for test case "<TestCaseId>"
    Then Verify step number "2" section "Product Details" displays the green completed icon
    Given user fills multiple commission details for test case "<TestCaseId>"
    Then Verify step number "3" section "Commission Details" displays the green completed icon
    ## ═════════════════════════════════════════════════════════════════════════
    ## 🚢  CREATE  ›  DELIVERY TERMS
    ## ═════════════════════════════════════════════════════════════════════════
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
    Then captures the "CommercialCommodityPO" "Contract No." into properties and json "contractDetails" "contractNo"

    And verify "Contract No." is in "@contract.pendingStatus" status
    Then verify the "@contract.pendingMenuItems" menu items is displayed for "Contract No."
    Then search for the json created "contractDetails" by "contractNo"

    When the user selects the "Purchase""checkbox" for the created Purchase Order
    Then apply a static wait for the web element for testing purposes only.

    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  APPROVAL FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    Then verify "@approval.approveButton""@approval.approveIcon" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "@approval.approveButton" button on the Commercial Commodity Contract Management page

    Then verify "@approval.confirmationMessage" message is displayed with "@approval.yesButton" and "@approval.noButton" buttons
    Then click on "@approval.yesButton" button

    Then captures the "CommercialCommodityPO" "Contract No." into properties and json "contractRecords" "approvedcontractNo"
    Then captures the "CommercialCommodityPO" "Contract No." into properties and json "grnProductDetails" "contractSI"

    Then verify the "@approval.approvedMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@approval.approvedStatus" status

    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  Goods Receipt Note Flow
    ## ═════════════════════════════════════════════════════════════════════════
    When user navigates to "Logistics" dashboard sidebar menu
    When user clicks on "Goods Receipt Note" section on the dashboard page

    When clicks on "Create" "create-new" icon on the Commodity Contract Management dashboard

    And verifies that the "Basic Details" section is displayed

    When user selects "Multiple GRN per Truck" custom radio in "Basic Details" section
    When user selects "Single GRN per Truck" custom radio in "Basic Details" section

    Then verify that the "GRN No." field in "Basic Details" section with value "@grnBasicDetails.grnNo" is "disabled" and displays "@grnBasicDetails.grnNo"
    When the user selects "Basic Details" "Date" as "today-1"

    When user selects "@grnBasicDetails.location" from the "Location" modified dropdown in the "Basic Details" section
    When user selects "@grnBasicDetails.counterparty" from the "Counterparty" modified dropdown in the "Basic Details" section

    When user enters "@grnBasicDetails.cpReferenceNo" in "CP Reference No." text field in the "Basic Details" section
    When the user selects "Basic Details" "CP Reference No." "Date" as "today-1"


    When user enters "@grnBasicDetails.otherReferenceNo" in "Other Reference No." text field in the "Basic Details" section
    When the user selects "Basic Details" "Other Reference No." "Date" as "today-1"

    Then user enters "@grnBasicDetails.remarks" in "Remarks" long text field in the "Basic Details" section

    When use click on "Search" button in the "Basic Details" section

    And verifies that the "Truck Details" section is displayed
    When user selects "@grnTruckDetails.transporter" from the "Transporter" modified dropdown in the "Truck Details" section
    When user enters "@grnTruckDetails.truckManufacture" in "Truck Manufacture" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.truckPlateNumber" in "Truck Plate Number" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.truckColor" in "Truck Color" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.tareWeight" in "Tare Weight" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.registrationCertificate" in "Registration Certificate" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.companyRefNumber" in "Company Ref Number" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.transporterReferenceNumber" in "Transporter Reference Number" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.driverName" in "Driver Name" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.driverLicenseNo" in "Driver License No." text field in the "Truck Details" section

    And verifies that the "Product Details" section is displayed
    Given user fills multiple grn product details for test case "<TestCaseId>"

    And click on "Save" button


    ## ═════════════════════════════════════════════════════════════════════════
    ##   TEST DATA  (one row per test case id in contractTestData.json)
    ## ═════════════════════════════════════════════════════════════════════════
    Examples:
      | TestCaseId | TestCaseDescription (No usage - only to outline each test case)                       |
      | TC_GRN_001 | Commodity flow with 1 product and 1 commission detail in Edge with admin profile      |

  @XBS_Commodity_Contract_Management_With_GRN_SI@RegressionTesting
  Scenario Outline: Create commodity contract management and verify Goods Receipt Note with SI.
    ## ═════════════════════════════════════════════════════════════════════════
        ## 🔐  SETUP & LOGIN
    ## ═════════════════════════════════════════════════════════════════════════
    Given test data "<TestCaseId>" is loaded
    And open browser

    When enter the user name as "@login.username"
    When enter the user password as "@login.password"
    Then the user should be navigated to the dashboard
#
    ## ═════════════════════════════════════════════════════════════════════════
    ## 🧭  NAVIGATION
    ## ═════════════════════════════════════════════════════════════════════════
    When user navigates to "Commercial" dashboard sidebar menu
    When user clicks on "Commodity Contract Management" section on the dashboard page

    When clicks on "Create" "create-new" icon on the Commodity Contract Management dashboard

    ## ═════════════════════════════════════════════════════════════════════════
    ## 📝  CREATE  ›  GENERAL SECTION
    ## ═════════════════════════════════════════════════════════════════════════
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
    When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section

    When user selects "@commissionDetailsValidation.type1" verify "@general.invoiceCurrency" value is displayed

    When user selects "@general.changedExchange" from the "@general.exchange" dropdown in the "General" section
    Then verify that the "Price Currency / UOM" field is displayed as "@general.changedExchangePriceCurrencyUom"

    When user selects "@commissionDetailsValidation.type2" verify "@general.changedExchangePriceCurrencyUom" value is displayed

    When user selects "@general.fixationBy" from the "Fixation By" literal dropdown in the "General" section
    When user selects "@general.fixationTerms" from the "Fixation Terms" dropdown in the "General" section

    ###When user selects "@general.invoiceCurrency" from the "Invoice Currency" dropdown in the "General" section
    When user selects "@general.paymentTerms" from the "Payment Terms" dropdown in the "General" section
    When user enters "@general.paymentTermsDescription" in "Payment Terms Description" long text area field in the "General" section
    When user selects "@general.taxStructure" from the "Tax Structure" dropdown in the "General" section
    When user selects "@general.withHoldingTaxStructure" from the "With Holding Tax Structure" dropdown in the "General" section

    ## ═════════════════════════════════════════════════════════════════════════
    ## 📦  CREATE  ›  PRODUCT & COMMISSION DETAILS
    ## ═════════════════════════════════════════════════════════════════════════
    Given user fills multiple product details for test case "<TestCaseId>"
    Given user fills multiple commission details for test case "<TestCaseId>"
    ## ═════════════════════════════════════════════════════════════════════════
    ## 🚢  CREATE  ›  DELIVERY TERMS
    ## ═════════════════════════════════════════════════════════════════════════
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
    Then captures the "CommercialCommodityPO" "Contract No." into properties and json "contractDetails" "contractNo"

    And verify "Contract No." is in "@contract.pendingStatus" status
    Then verify the "@contract.pendingMenuItems" menu items is displayed for "Contract No."
    Then search for the json created "contractDetails" by "contractNo"

    When the user selects the "Purchase""checkbox" for the created Purchase Order
    Then apply a static wait for the web element for testing purposes only.
    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  APPROVAL FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    Then verify "@approval.approveButton""@approval.approveIcon" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "@approval.approveButton" button on the Commercial Commodity Contract Management page

    Then verify "@approval.confirmationMessage" message is displayed with "@approval.yesButton" and "@approval.noButton" buttons
    Then click on "@approval.yesButton" button

#    And verify the message "@approval.approvedMessage" is displayed
    Then capture "CommercialCommodityPO" "Contract No."

    Then verify the "@approval.approvedMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@approval.approvedStatus" status

    Then capture "CommercialCommodityPO" "Contract No." in json test data
    Then capture "CommercialCommodityPO" "Contract No."
    Then captures the "CommercialCommodityPO" "Contract No." into properties and json "contractShippingDetails" "contractNo"

    #═════════════════════════════════════════════════════════════════════════
    ## ✅  Shipping Instruction Flow
    ## ═════════════════════════════════════════════════════════════════════════
    When user navigates to "Logistics" dashboard sidebar menu
    When user clicks on "Shipping Instruction" section on the dashboard page

    When clicks on "Create" "create-new" icon on the Commodity Contract Management dashboard
    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  Contract Details Section
    ## ═════════════════════════════════════════════════════════════════════════
    When user selects "#CommercialCommodityPOContractNumber" from the "Contract No." modified dropdown in the "Contract Selection" section
    ## ✅  Instruction Details Section
    ## ═════════════════════════════════════════════════════════════════════════
    When the user selects the "Instruction details" "Date" as "today"
    Then user enters "@instructionDetails.bookingRefNo" in "Booking Ref No." text field in the "Instruction details" section
    When user selects "@instructionDetails.shipper" from the "Shipper" modified dropdown in the "Instruction details" section
    When user selects "@instructionDetails.shipToLocation" from the "Ship To Location" modified dropdown in the "Instruction details" section
    When user selects "@instructionDetails.product" from the "Product" modified dropdown in the "Instruction details" section
    Then user enters "@instructionDetails.units" in "Units" text field in the "Instruction details" section
    When user selects "@instructionDetails.packing" from the "Packing" modified dropdown in the "Instruction details" section
    Then verify the validation message "Qty. Must Be Less Than Or Equal To Balance Qty" is displayed
    Then user enters "@instructionDetails.qty" in "Qty." text field in the "Instruction details" section

    When the user selects the "Instruction details" "From Date" as "today+2"
    When the user selects the "Instruction details" "To Date" as "today-4"
    Then verify the validation message "To Date Cannot Be Prior To From Date" is displayed
    When the user selects the "Instruction details" "To Date" as "today+5"

     ## ═════════════════════════════════════════════════════════════════════════
     ## ✅  Shipment Details Section
#    ## ═════════════════════════════════════════════════════════════════════════
    When user selects "@shipmentDetails.shippingLine" from the "Shipping Line" modified dropdown in the "Shipment Details" section
    When user selects "@shipmentDetails.forwardingAgent" from the "Forwarding Agent" modified dropdown in the "Shipment Details" section
    When user selects "@shipmentDetails.freightTerms" from the "Freight Terms" modified dropdown in the "Shipment Details" section
    When user selects "@shipmentDetails.containerStatus" from the "Container Status" modified dropdown in the "Shipment Details" section

    When the user selects the type "Shipment Details" "ETA at Load Port" as "today+5"
    When the user selects the type "Shipment Details" "ETD" as "today+10"
    When the user selects the type "Shipment Details" "at Discharge Port" as "today+20"

    Then user enters "@shipmentDetails.noOfContainers" in "No. Of Containers" text field in the "Shipment Details" section
    When user selects "@shipmentDetails.consignee" from the "Consignee" modified dropdown in the "Shipment Details" section
    Then user enters "@shipmentDetails.consigneeDescription" in "Consignee Description" long text field in the "Shipment Details" section

    When user selects "@shipmentDetails.notify1" from the "Notify 1" modified dropdown in the "Shipment Details" section
    Then user enters "@shipmentDetails.notify1Description" in "Notify 1 Description" long text field in the "Shipment Details" section
    When user selects "@shipmentDetails.notify2" from the "Notify 2" modified dropdown in the "Shipment Details" section
    Then user enters "@shipmentDetails.notify2Description" in "Notify 2 Description" long text field in the "Shipment Details" section
    When user selects "@shipmentDetails.notify3" from the "Notify 3" modified dropdown in the "Shipment Details" section
    Then user enters "@shipmentDetails.notify3Description" in "Notify 3 Description" long text field in the "Shipment Details" section
    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  Bag Marks Table
    ## ═════════════════════════════════════════════════════════════════════════
    When the user enters "@bagMarks.bagMark1" in row "1" of the "Bag Marks" table
    When the user enters "@bagMarks.bagMark2" in row "2" of the "Bag Marks" table
    When the user enters "@bagMarks.bagMark3" in row "3" of the "Bag Marks" table
    When the user enters "@bagMarks.bagMark4" in row "4" of the "Bag Marks" table
    When the user enters "@bagMarks.bagMark5" in row "5" of the "Bag Marks" table
    ## ═════════════════════════════════════════════════════════════════════════
    ## 📄  CREATE  ›  DOCUMENTS
    ## ═════════════════════════════════════════════════════════════════════════
    ##When user selects "@modifyDocuments.documentSet" from the "Document Set" dropdown in the "Documents" section
    When the user selects the SI checkboxes listed in test data "modifyDocuments.select" under "Document Set"
    When the user deselects the SI checkboxes listed in test data "modifyDocuments.deselect" under "Document Set"
    ## ═════════════════════════════════════════════════════════════════════════
    ## 💬  CREATE  ›  REMARKS & INSTRUCTIONS
    ## ═════════════════════════════════════════════════════════════════════════
    When user enters "@remarks.remarks" in "Remarks" long text area field in the "Remarks and Instructions" section
    When user enters "@remarks.instructions" in "Instructions" long text area field in the "Remarks and Instructions" section

    And click on "Save" button
    Then captures the "ShippingInstruction" "SI No." into properties and json "contractShippingDetails" "shippingInstruction"

    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  APPROVAL FLOW
    ## ═════════════════════════════════════════════════════════════════════════
    And verify "Contract No." is in "@contract.pendingStatus" status
#    Then search for the json created "contractShippingDetails" by "shippingInstruction"

    When the user selects the "Purchase""checkbox" for the created Purchase Order
    Then apply a static wait for the web element for testing purposes only.

    Then verify "@approval.approveButton""@approval.approveIcon" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    When the user clicks the "@approval.approveButton" button on the Commercial Commodity Contract Management page

    Then verify "@approval.confirmationMessage" message is displayed with "@approval.yesButton" and "@approval.noButton" buttons
    Then click on "@approval.yesButton" button

#    And verify the message "@approval.approvedMessage" is displayed
    Then capture "ApprovedShippingInstruction" "SI No."
    Then captures the "ApprovedShippingInstruction" "SI No." into properties and json "contractShippingDetails" "approvedShippingInstruction"

#    Then verify the "@approval.approvedMenuItems" menu items is displayed for "Contract No."
    And verify "Contract No." is in "@approval.approvedStatus" status

    ## ═════════════════════════════════════════════════════════════════════════
    ## ✅  Goods Receipt Note Flow
    ## ═════════════════════════════════════════════════════════════════════════
    When user navigates to "Logistics" dashboard sidebar menu
    When user clicks on "Goods Receipt Note" section on the dashboard page

    When clicks on "Create" "create-new" icon on the Commodity Contract Management dashboard

    And verifies that the "Basic Details" section is displayed

    When user selects "Multiple GRN per Truck" custom radio in "Basic Details" section
    When user selects "Single GRN per Truck" custom radio in "Basic Details" section

    Then verify that the "GRN No." field in "Basic Details" section with value "@grnBasicDetails.grnNo" is "disabled" and displays "@grnBasicDetails.grnNo"
    When the user selects "Basic Details" "Date" as "today-1"

    When user selects "@grnBasicDetails.location" from the "Location" modified dropdown in the "Basic Details" section
    When user selects "@grnBasicDetails.counterparty" from the "Counterparty" modified dropdown in the "Basic Details" section

    When user enters "@grnBasicDetails.cpReferenceNo" in "CP Reference No." text field in the "Basic Details" section
    When the user selects "Basic Details" "CP Reference No." "Date" as "today-1"


    When user enters "@grnBasicDetails.otherReferenceNo" in "Other Reference No." text field in the "Basic Details" section
    When the user selects "Basic Details" "Other Reference No." "Date" as "today-1"

    Then user enters "@grnBasicDetails.remarks" in "Remarks" long text field in the "Basic Details" section

    When use click on "Search" button in the "Basic Details" section

    And verifies that the "Truck Details" section is displayed
    When user selects "@grnTruckDetails.transporter" from the "Transporter" modified dropdown in the "Truck Details" section
    When user enters "@grnTruckDetails.truckManufacture" in "Truck Manufacture" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.truckPlateNumber" in "Truck Plate Number" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.truckColor" in "Truck Color" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.tareWeight" in "Tare Weight" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.registrationCertificate" in "Registration Certificate" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.companyRefNumber" in "Company Ref Number" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.transporterReferenceNumber" in "Transporter Reference Number" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.driverName" in "Driver Name" text field in the "Truck Details" section
    When user enters "@grnTruckDetails.driverLicenseNo" in "Driver License No." text field in the "Truck Details" section

    And verifies that the "Product Details" section is displayed
    Given user fills multiple grn product details for test case "<TestCaseId>"

    And click on "Save" button


    ## ═════════════════════════════════════════════════════════════════════════
    ##   TEST DATA  (one row per test case id in contractTestData.json)
    ## ═════════════════════════════════════════════════════════════════════════
    Examples:
      | TestCaseId | TestCaseDescription (No usage - only to outline each test case)                       |
      | TC_GRN_SI_001 | Commodity flow with 1 product and 1 commission detail in Edge with admin profile      |
