---
name: erpxbs-domain
description: ERPXBS business and test-domain knowledge for contracts, amendments, GRN/GDN, ticketing, invoicing, inventory, pro-forma invoices, and PnL. Use when writing or reviewing ERPXBS tests, scenarios, workflows, or business-rule documentation.
user-invocable: false
---

# ERPXBS Domain Knowledge

## Purpose and source boundaries

This agent summarizes the ERPXBS domain scenarios and expected behaviors recorded in the supplied `ERPXBS-Domain.xlsx`. The workbook is a test-scenario and workflow source; it does not describe the software architecture, API endpoints, database schema, or automation selectors. Do not invent those details.

Treat statements marked as expected outcomes in the workbook as requirements to verify, not as proof that the running application already behaves that way. When a scenario is marked as needing clarity, keep it open and ask for a business decision before asserting an expected result.

The workbook sheets used are `Amendment`, `Profitloss product`, `UOM cases`, `purchase pro format`, `Dependencies check`, `GRN latest changes`, `Purchase flows`, and `Purchase`. `SI cancel` and `Sheet10` contain no substantive scenario content in the supplied workbook.

## ERPXBS overview

The workbook covers commodity purchase and sales operations, inventory movements, physical ticketing, contract lifecycle changes, invoice processing, and month-end profit and loss. Its repeated lifecycle pattern is:

1. Create a purchase or sales contract and submit/approve it.
2. Amend, roll, convert, force-close, or cancel the contract when applicable.
3. Execute downstream logistics or inventory flows such as Shipping Instruction (SI), ticketing, Goods Receipt Note (GRN), or Goods Dispatch Note (GDN).
4. Process purchase price calculation (PPC), pro-format invoice, invoice, voucher, and related inventory operations as applicable.
5. Verify the contract dashboard, operational screen, reports, validations, and PnL impact.

The scenarios distinguish purchase from sales and cover Flat Price, PTBF, and NPE contract classes or pricing modes. Preserve the exact terms used by the workbook in tests and documentation; do not silently normalize uncertain abbreviations.

## Additional coverage reflected in the current feature suite and JSON data

The feature files in `src/test/resources/features` and the JSON dataset in `src/test/resources/testdata/contractTestData.json` validate the same ERPXBS lifecycle in executable form. They confirm the following project-specific flow coverage that should be treated as part of the domain context when drafting scenarios or updating test data.

### Contract creation and validation flows

- `CommodityContractManagementFlatPrice .feature`, `CommodityContractManagementPTBF.feature`, and `CommodityContractManagementNPE.feature` all exercise a common contract-creation lifecycle for purchase contracts.
- Mandatory validation is checked before saving, including at minimum `Class`, `Pricing`, and the section-level required fields for `General`, `Product Details`, and `Delivery Terms`.
- Validation steps cover `Port Of Loading` and `Port Of Discharge` cannot be the same, exchange-driven `Price Currency / UOM` updates, and `Document Set` selection/deselection behavior.
- Contract creation requires the user to complete general data, product details, commission details, delivery terms, remarks, instruction notes, and document selections. The feature suite verifies the created contract persists in `Pending` status and exposes expected menu items such as `Modify`, `Copy By Contract`, and `Cancel`.
- Approval is validated through the contract dashboard: the contract is approved, the approval confirmation popup is handled, and the contract status is re-checked in `Approved` state.

### Amendment and modification flows

- After approval, each contract flow verifies the amendment screen, including locked fields like `Class` and `Pricing` and the `Amend No.` state.
- The amendment flow updates general, product, commission, and delivery-term data using dataset values and verifies the amendment is accepted as a valid modification.
- The project JSON stores per-test-case amendment and modify values such as `billingLocation`, `shippingLocation`, `counterparty`, `shipper`, `invoiceCurrency`, `portOfLoading`, `portOfDischarge`, `termsAndConditions`, `remarks`, `instructions`, and `internalRemarks`.
- The dataset also tracks `secondAmendment` values and `contractAmendment` confirmation text, menu states, and cancel reasons used in later validations.

### Shipping Instruction and GRN flows

- `ShippingInstruction.feature` covers the Shipping Instruction (SI) lifecycle from an approved contract. It verifies contract selection, default values copied from the contract, SI validation for qty and date ranges, shipping line/freight details, document checklist selection, and record creation.
- Required SI checks include `Qty. Must Be Less Than Or Equal To Balance Qty`, `To Date Cannot Be Prior To From Date`, and explicit contract/ship-to/location data population from the source contract.
- `GoodsReceiptNote.feature` covers GRN creation after contract approval, including `Single GRN per Truck` and `Multiple GRN per Truck` handling, truck and transporter details, product details capture, and the GRN save flow.
- The JSON file stores GRN-related keys such as `contractShippingDetails.contractNo`, `contractRecords.approvedcontractNo`, `grnProductDetails.contractSI`, and `grnBasicDetails`/`grnTruckDetails` values used by the test automation.

### JSON dataset structure and scenario coverage

The dataset file `contractTestData.json` is not just static fixture data; it is the scenario matrix for the feature suite. It includes scenario IDs such as:

- `TC_CCM_001` to `TC_CCM_004` for core Flat Price contract flows
- `TC_CCM_NPE_001` for NPE contract coverage
- `TC_CCM_PTBF_001` for PTBF contract coverage
- `TC_CCM_SI_001` for shipping instruction flow
- `TC_GRN_001` and `TC_GRN_SI_001` for GRN and GRN-with-SI scenarios

The JSON blocks carry the exact values used by the automation for login, contract defaulting, validation data, product rows, commissions, document sets, approval states, and downstream logistic records. When a test references `@general.*`, `@deliveryTerms.*`, `@modify.*`, `@amendment.*`, `@instructionDetails.*`, or `@grnBasicDetails.*`, it is consuming values defined in this file.

### Domain rule notes for implementation teams

When using the feature suite as the source of truth, preserve the following project-specific rules:

- `Trader` is automatically populated from the logged-in user during contract creation and amendment.
- Exchange changes update the displayed `Price Currency / UOM` field and must be validated accordingly.
- Contract approval marks the state as `Approved`; the `Pending` state is only valid before approval.
- Non-amendable fields such as `Class` and `Pricing` remain locked after contract creation and approval.
- Downstream logistics flows (SI and GRN) rely on contract values as source data and validate remaining qty/date rules before save.
- The automation captures and reuses created contract numbers in JSON so later flows can select the right contract without manual recomposition.

## Core business rules captured by the workbook

### Contract amendments

The `Amendment` sheet lists each field and the transaction markers that restrict changing it. Keep these rules field-specific:

| Field | Workbook restriction |
|---|---|
| Class | Cannot be amended after contract creation |
| Pricing | Cannot be amended after contract creation |
| Contract Number | Cannot be amended after contract creation and approval |
| Contract Date | Cannot be amended after contract creation and approval |
| Qty | Cannot be reduced below Force Close plus the greater quantity among GRN/GDN, Shipping Instruction, Price Fixation, Sales Blocking, Pro-forma, and Allocation |
| Target Date Range | From Date cannot be amended after GRN/GDN; To Date can be amended any time |
| Trader, Shipper, CP Ref 1, CP Ref 2, Payment Terms, Payment Terms Description, Units, Packing, Target Type, Trade Month, Position Month, Quality, Commission Agent/Broker, Ref. No., Type, Value, INCO Terms, Place, Basis of Sale, Port of Loading, POL Description, Port of Discharge, POD Description, Final Destination, Weight Terms, Country of Origin, Arbitration, Terms and Conditions, Document Set, Remarks, Instructions, Internal Remarks | Can be amended any time |

Other field restrictions are recorded by lifecycle markers in the workbook. Preserve those markers when interpreting the table rather than guessing a different rule:

| Marker | Meaning in workbook |
|---|---|
| G | GRN/GDN |
| S | Shipping Instruction |
| F | Force Close |
| R | Ticket Review |
| A | Ticket Allocation |
| X | Price Fixation |
| P | Price Calculation |
| I | Invoice |
| B | Sales Blocking |
| PF | Pro-forma |

The source labels some fields with one or more of these markers (for example, Billing Location `I`, Shipping Location `GSRA`, Counter Party `GSRA`, Exchange `GSRAX`, Invoice Currency `P`, Price Currency/UOM `XP`, Tax Structure `I`, Withholding Tax Structure `I`, Product `GSAXP`, UOM `GSFRAX`, Price `PI`, Differential `XPI`, and Certification & Premium `GRA`). Do not infer undocumented combinations; consult the workbook or request clarification if a marker is unclear.

### Purchase and sales contract lifecycle

The `Purchase flows` sheet covers both sides of the contract process:

- Create and edit a contract, save it, submit it for approval, and approve it.
- After approval, create an amendment, submit and approve it, or cancel it. Include successive amendments and cancellation of a later amendment.
- Perform partial or complete force close. Scenarios also check cancellation attempts after force close.
- Verify expected contract visibility and current values in the relevant dashboard and Commodity Contract report.

For PTBF Contract Rolling scenarios, an approved PTBF contract should be available in the rolling screen. Changes to date, trade month, target date, or differential should update the contract and generate an auto-approved amendment. The target date must not precede the contract date, and the trade month must not be the current month. A successful rolling submission should be reflected when the contract is later amended from its dashboard.

Force-close scenarios cover Purchase and Sales, filtering by side and contract date range, displaying approved contracts, partial and complete force close, and force-close dates that must not precede the contract date. Force Close with Tolerance scenarios cover item-level quantity and percentage criteria: an action within the configured tolerance should submit successfully, and exceeding the configured tolerance should show validation. The workbook has inconsistent wording in a few filter rows; use the scenario context and verify the correct side rather than copying a likely typo.

### NPE conversions

The workbook contains Purchase and Sales scenarios for NPE contracts with one or multiple items. Scenarios include conversion to Flat Price or PTBF, conversion cancellation, amendment after conversion, addition of items, price or differential updates, and downstream SI/GRN/GDN/ticket allocation flows.

When a scenario includes a downstream transaction, verify whether that transaction blocks cancellation of the conversion as specified by that scenario. Do not generalize one cancellation outcome to all NPE conversions. Rolling should show only eligible PTBF contracts when that is the stated scenario; multi-contract rolling is also included.

### GRN and GDN

The `GRN latest changes` sheet groups GRN coverage by truck and contract/product shape:

- Single GRN per truck: one contract/one batch; one contract/multiple batches of the same product; one contract/multiple batches with different products.
- Multiple GRNs per truck: multiple contracts/multiple batches of the same product; multiple contracts/multiple batches with different products.

The broader `Purchase flows` sheet also covers direct GRN creation for Purchase and direct GDN creation for Sales, with single- and multi-product ticketing flows. For Sales GDN, the workbook includes partial dispatch followed by force-closing the remaining quantity, invoice generation and cancellation checks, and amendment-related dispatch validation.

GRN/GDN cancellation behavior depends on downstream dependencies. Check the exact transaction chain before asserting that cancellation is allowed or blocked.

### Ticketing and inventory dependencies

Ticketing scenarios cover direct ticket creation and a multi-stage flow: create and approve a ticket, record first weight, perform Warehouse Intake/Dispatch (WID), record second weight, then allocate to a contract. Multi-product tickets include cases where allocation is made for only one product, ticket/product details are edited, and cancellation is attempted after allocation. The workbook expects cancellation to be blocked in specified post-allocation cases.

Inventory Process cases include Processing Instruction (PI), processing issue/receipt, WID, stock transfer, Stock Adjustment (SA), batch merge/split, and Sales Blocking. They test whether a batch reserved or consumed in one process is excluded from another and whether a transaction can be cancelled after dependent work exists.

Specific dependencies recorded in `Dependencies check` include:

- Purchase GRN linked to SI: cancelling the SI after GRN should be blocked.
- GRN with QA recorded: GRN cancellation is expected to be allowed in the listed case.
- GRN with PPC: cancelling the GRN should be blocked.
- PPC followed by Invoice: cancelling PPC should be blocked.
- Invoice followed by a manual debit/credit note: cancelling the invoice should be blocked.
- PTBF Purchase with physical allocation and PPC: deallocation from futures should be blocked after PPC.
- PTBF Purchase invoice/physical allocation/PPC/finalization: invoice cancellation should be blocked.
- Sales GDN/invoice and invoice debit/credit-note dependencies: cancellation should be blocked in the listed cases.
- Stock transfer, processing, ticket allocation, and batch merge/split cases: follow the specific scenario's stated validation and cancel expectations.

### Pro-forma invoices

The `purchase pro format` sheet covers Flat Price, PTBF, and NPE Purchase contracts. The scenarios include single- and multi-product contracts, pro-forma against a contract before GRN, pro-forma against a GRN, partial quantities, positive and negative pro-forma invoices, contract amendments, force close, currency combinations (INR/INR, USD/USD, USD/INR, INR/USD), price fixation, and NPE conversion.

Expected checks captured in the workbook:

- A positive pro-forma may be created up to the eligible contract quantity/value in the stated cases.
- After a partial GRN and pro-forma, a later pro-forma must respect the remaining eligible contract/GRN balance described in that case.
- A negative pro-forma must not exceed the positive pro-forma amount in the listed Flat Price cases.
- A fully force-closed contract should not appear for a new pro-forma in the stated case.
- After an approved increase in contract quantity, a later pro-forma may use the new eligible quantity.
- A pro-forma against a GRN is expected to prevent GRN cancellation in the listed case.

The source explicitly flags these questions as unresolved; do not invent expected behavior:

1. PTBF multi-product contract where only one product is price-fixed: the expected pro-forma behavior needs clarification.
2. Negative PTBF pro-forma created after price fixation: whether it should show provisional or fixed price needs clarification.
3. Whether a contract can be fully force-closed after a pro-forma has been created needs clarification.

### UOM and ledger behavior

The `UOM cases` sheet includes:

- Purchase PTBF GRN and invoice/return checks with contract quantity UOM in MT and LB.
- Purchase NPE direct GRN with MT and invoice.
- Sales Flat Price, PTBF, and NPE multi-product invoices where products use the same UOM.
- A Sales Flat Price multi-product invoice with different UOMs; the listed expectation is that invoice creation is rejected with validation.
- Import and Local Purchase/Sales direct GRN/GDN flows that check the ledger name in the invoice screen's Voucher Posting section.

The sheet does not define conversion formulas or a full supported UOM matrix. Assert only the scenario's stated expectation and verify calculations against the referenced XBS reports.

### PnL

The `Profitloss product` sheet covers provisional and finalized PnL, purchase and sales contracts, contract changes, GRN, PPC, invoices, cost accruals, and inventory report reconciliation.

- A mid-month PnL calculation should offer Provisional rather than Finalize.
- On the last day of the month, if Month Close has not been completed, the option should remain Provisional.
- Finalize should be available only when the PnL is calculated on the last day of the month and that month is closed in Month Close.
- PnL calculation should stop when the month has unapproved contracts, invoices, or vouchers.
- Opening Balance should match Current Stock, Consolidated Stock, and the previous month's PnL closing balance. Use Transaction Details to reconcile values across reports.
- Open Contracts should reflect approved and effective contract state. An unapproved new contract should not affect the report; an unapproved amendment should not be reflected, and its approved version should be reflected.
- Verify quantity, price, and total amount for Flat Price, PTBF, and NPE. Add cost accrual and confirm its PnL effect.
- Approved amendments should update PnL; cancelled amendments should leave original details. Partial force close should update open quantity; cancelled force close should restore the prior state; complete force close should remove the contract from Open Contracts.
- NPE conversion to Flat Price or PTBF and cancellation should update or restore the open contract as described in the scenario.
- GRN, cost accrual on GRN, PPC, and Invoice scenarios compare same-month and next-month PnL and impacted reports/elements.

## Test design guidance

When writing ERPXBS tests from these sources:

1. Identify the contract side, class/pricing mode, product count, quantity/UOM, and transaction chain before choosing data.
2. State the preconditions and approval state. Do not treat a created, unapproved contract or amendment as approved.
3. Verify both the action result and downstream state: visible contract/transaction, open balance, report values, cancellation availability, or validation.
4. For cancel tests, create the exact dependent transaction in the scenario first. Assert only the dependency and outcome documented for that chain.
5. For financial or quantity calculations, compare the operational screen with the named XBS report(s), including balances after amendments, force close, GRN/GDN, PPC, invoice, and return.
6. Use positive and negative cases around quantity, tolerance, UOM, approval, month close, and remaining contract/GRN balance.
7. Keep Purchase and Sales paths separate unless a scenario explicitly covers both.
8. Preserve workbook ambiguities and mark them as open questions. Do not make up UI selectors, endpoints, schemas, permissions, exact error text, or accounting formulas.

## Reference scenarios by workbook area

| Workbook sheet | Main coverage |
|---|---|
| `Amendment` | Field-level amendment restrictions and lifecycle markers |
| `Purchase flows` | Purchase/Sales contract process, ticketing, inventory, GRN/GDN, NPE conversion, rolling, and force close |
| `GRN latest changes` | One/multiple GRNs per truck across contracts, batches, and products |
| `Dependencies check` | Cancellation dependencies across SI, GRN, QA, PPC, invoice, allocations, processing, stock transfers, and batch changes |
| `purchase pro format` | Pro-forma quantity/value, negative invoices, currency, GRN, force close, PTBF price fixation, and NPE conversions |
| `UOM cases` | MT/LB and mixed UOM invoice scenarios; Import/Local ledger verification |
| `Profitloss product` | PnL provisional/finalize, month close, approvals, contract state, cost accrual, inventory balances, and GRN/PPC/invoice timing |
| `Purchase` | Sample Purchase contract-to-SI-to-ticket/GRN workflow and report checks |

