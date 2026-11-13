# MG GDN (Goods Dispatch Note) — Create

## Scope and evidence

These scenarios cover only the **Goods Dispatch Note - Create** form shown in the supplied UI screenshot: Basic Details, Search, Save/Cancel, and the displayed step 2/4 progress. This is not a full application suite.

The required indicators and fields below are taken from the screenshot: GDN No. (AUTO), GDN Date, Location, Product, and Counterparty are required; Ref No., Ref Date, and Remarks are not marked required. The ERP domain document records direct GDN creation for Sales and downstream partial dispatch, force-close, invoice, and cancellation flows. Those downstream flows are outside this create-form scope.

No existing `docs/` scenario document was present to establish a separate document convention. The existing `src/test/resources/features/GoodsDispatchNote.feature` is empty, and no GDN implementation/page-object behavior was found in the inspected source. Therefore, behavior not evidenced by the screenshot or domain document is explicitly marked **unspecified / characterize** below. Do not treat proposed checks about validation timing, error copy, generated-number format, permissions, date range, or blocked-batch effects as confirmed product rules.

### TC-001: Create a GDN with valid basic details
**Category**: Happy Path  
**Priority**: P1  
**Preconditions**: User is signed in and can create GDNs; valid Location, Product, and Counterparty choices are available.  
**Steps**:
1. Open **Goods Dispatch Note - Create**.
2. Confirm GDN No. is shown as AUTO; enter a valid GDN Date.
3. Select a Location, Product, and Counterparty; optionally enter Ref No., Ref Date, and Remarks.
4. Use Search and complete only the actions presented by the create flow; select Save.
**Expected Results**: The form accepts the valid required details and reports a successful save or next-flow state. The resulting GDN is identifiable in the application. Exact confirmation text, post-save destination, and any remaining steps are unspecified and should be characterized.  
**Business Rule**: The screenshot marks GDN Date, Location, Product, and Counterparty as required; the domain documents direct GDN creation for Sales.  
**Suggested Layer**: E2E

### TC-100: Enforce the required-field set shown on the form
**Category**: Business Rule  
**Priority**: P1  
**Preconditions**: Create form is open.  
**Steps**:
1. Inspect the labels and required indicators for GDN No., GDN Date, Ref No., Ref Date, Remarks, Location, Product, and Counterparty.
2. Leave required fields unfilled and attempt the relevant Search/Save action.
**Expected Results**: GDN Date, Location, Product, and Counterparty are treated as required; GDN No. is system-generated (AUTO). Ref No., Ref Date, and Remarks are not required according to the screenshot. Validation timing and wording are unspecified.  
**Business Rule**: Required fields are those visibly marked required in the supplied screenshot.  
**Suggested Layer**: Component

### TC-101: Generate GDN No. automatically
**Category**: Business Rule  
**Priority**: P1  
**Preconditions**: Create form is open.  
**Steps**:
1. Observe GDN No. on initial load and after selecting the other Basic Details.
2. Proceed through Search/Save without manually composing a number.
3. Record the displayed/saved GDN No.  
**Expected Results**: The number is provided by the system as indicated by AUTO and the user is not required to supply a number. Number format, editability, generation timing, and uniqueness policy are unspecified; record actual behavior rather than asserting an undocumented format.  
**Business Rule**: The screenshot labels required GDN No. as AUTO.  
**Suggested Layer**: E2E

### TC-102: Keep GDN Date and Ref Date independent
**Category**: Business Rule  
**Priority**: P2  
**Preconditions**: Create form is open.  
**Steps**:
1. Enter one valid date in GDN Date.
2. Enter a different valid date in Ref Date.
3. Search/save and inspect the resulting GDN details if they are available in the flow.
**Expected Results**: GDN Date is required and Ref Date is optional; the two inputs accept and retain their separately entered values. Any date ordering or range rule is unspecified.  
**Business Rule**: The screenshot shows distinct GDN Date and Ref Date inputs, with only GDN Date marked required.  
**Suggested Layer**: Component

### TC-103: Keep the create flow on the Sales GDN path
**Category**: Business Rule  
**Priority**: P1  
**Preconditions**: A valid Sales-side GDN workflow context and corresponding master-data selections are available.  
**Steps**:
1. Open the GDN Create flow from the Sales workflow.
2. Select the applicable Location, Product, and Counterparty and search.
3. Complete the create action and inspect the resulting transaction context.
**Expected Results**: The created transaction is a Sales GDN, not a Purchase GRN. Which Sales source records are eligible and how the form exposes that relationship are unspecified by the screenshot; verify against the workflow/data available in the environment.  
**Business Rule**: The domain document distinguishes direct GDN creation for Sales from direct GRN creation for Purchase.  
**Suggested Layer**: E2E

### TC-104: Allow optional reference and remarks details to be omitted
**Category**: Business Rule  
**Priority**: P2  
**Preconditions**: Valid required details are available.  
**Steps**:
1. Leave Ref No., Ref Date, and Remarks blank.
2. Complete the required details and use Search/Save.
**Expected Results**: The absence of these unmarked fields does not itself block the form. Any conditional requirements introduced by another step are unspecified.  
**Business Rule**: Ref No., Ref Date, and Remarks are not marked required in the screenshot.  
**Suggested Layer**: Component

### TC-200: Prevent unauthenticated access to GDN creation
**Category**: Security  
**Priority**: P1  
**Preconditions**: A user session is signed out; an authorized test environment is available.  
**Steps**:
1. Attempt to open the GDN Create route/page without signing in.
2. If the form was already open, let the session expire and attempt to Search/Save.
**Expected Results**: The application applies its configured authentication policy and does not permit an unauthenticated GDN to be created. Redirect/message behavior is unspecified.  
**Business Rule**: Authentication is a security control to verify; the supplied domain and screenshot do not specify session behavior.  
**Suggested Layer**: E2E

### TC-201: Enforce the configured create permission
**Category**: Security  
**Priority**: P1  
**Preconditions**: A signed-in test account without the configured GDN-create permission and an account with permission are available. The role/permission matrix must be confirmed for the environment.  
**Steps**:
1. Sign in as the account without create permission.
2. Attempt to open the create screen and, if reachable, attempt Search/Save.
3. Repeat with an account that has create permission as a control.
**Expected Results**: The configured authorization policy is enforced and a user lacking create permission cannot create a GDN; the authorized control account can reach the permitted flow. Which roles have permission and the exact denial behavior are unspecified.  
**Business Rule**: No role matrix is provided; this scenario verifies configured authorization without assuming particular role names.  
**Suggested Layer**: E2E

### TC-300: Block save when GDN Date is missing
**Category**: Negative  
**Priority**: P1  
**Preconditions**: Create form is open; valid Location, Product, and Counterparty choices are available.  
**Steps**:
1. Leave GDN Date blank.
2. Select Location, Product, and Counterparty.
3. Attempt to Search/Save as permitted by the flow.
**Expected Results**: A GDN is not successfully created without the required GDN Date. Exact validation message and whether validation occurs at Search or Save are unspecified.  
**Business Rule**: GDN Date is marked required in the screenshot.  
**Suggested Layer**: Component

### TC-301: Block save when Location is missing
**Category**: Negative  
**Priority**: P1  
**Preconditions**: Create form is open; valid GDN Date, Product, and Counterparty are available.  
**Steps**:
1. Leave Location unselected.
2. Enter/select the other required values.
3. Attempt to Search/Save as permitted by the flow.
**Expected Results**: A GDN is not successfully created without a Location. Exact validation message and timing are unspecified.  
**Business Rule**: Location is marked required in the screenshot.  
**Suggested Layer**: Component

### TC-302: Block save when Product is missing
**Category**: Negative  
**Priority**: P1  
**Preconditions**: Create form is open; valid GDN Date, Location, and Counterparty are available.  
**Steps**:
1. Leave Product unselected.
2. Enter/select the other required values.
3. Attempt to Search/Save as permitted by the flow.
**Expected Results**: A GDN is not successfully created without a Product. Exact validation message and timing are unspecified.  
**Business Rule**: Product is marked required in the screenshot.  
**Suggested Layer**: Component

### TC-303: Block save when Counterparty is missing
**Category**: Negative  
**Priority**: P1  
**Preconditions**: Create form is open; valid GDN Date, Location, and Product are available.  
**Steps**:
1. Leave Counterparty unselected.
2. Enter/select the other required values.
3. Attempt to Search/Save as permitted by the flow.
**Expected Results**: A GDN is not successfully created without a Counterparty. Exact validation message and timing are unspecified.  
**Business Rule**: Counterparty is marked required in the screenshot.  
**Suggested Layer**: Component

### TC-304: Handle Search with incomplete Basic Details
**Category**: Negative  
**Priority**: P2  
**Preconditions**: Create form is open.  
**Steps**:
1. Leave one or more required Search details unselected or blank.
2. Select Search.
**Expected Results**: The application does not treat an incomplete search as a successful GDN creation or display misleading matches. Whether it blocks Search or returns an empty result, and the message text, are unspecified.  
**Business Rule**: The form marks GDN Date, Location, Product, and Counterparty required; Search behavior is not described in the domain or source code.  
**Suggested Layer**: Component

### TC-305: Handle Search with no matching records
**Category**: Negative  
**Priority**: P2  
**Preconditions**: Create form is open; test data can be chosen that produces no matching Search result, if Search is data-dependent.  
**Steps**:
1. Enter/select valid required Basic Details for which no match exists.
2. Select Search.
**Expected Results**: No unrelated record is presented as a match, and the user can recover or revise the search. Empty-state wording and whether a match is required to proceed are unspecified.  
**Business Rule**: Search is visible in the screenshot; match criteria and empty-result behavior are not documented.  
**Suggested Layer**: E2E

### TC-306: Reject an invalid GDN Date
**Category**: Negative  
**Priority**: P1  
**Preconditions**: Create form is open; valid Location, Product, and Counterparty choices are available.  
**Steps**:
1. Attempt to enter an invalid/unparseable value in GDN Date using the available date input.
2. Complete the other required fields and attempt to proceed/save.
**Expected Results**: An invalid date is not accepted as a successfully saved GDN Date. Input format, date range, and exact validation behavior are unspecified.  
**Business Rule**: GDN Date is a required date input in the screenshot; no date bounds are documented.  
**Suggested Layer**: Component

### TC-307: Avoid creating a GDN when the user cancels
**Category**: Negative  
**Priority**: P2  
**Preconditions**: Create form is open and unsaved changes have been entered.  
**Steps**:
1. Select Cancel.
2. If a confirmation is presented, choose the option that abandons the draft.
3. Reopen the create flow and check whether the abandoned draft was created.
**Expected Results**: The user can abandon the unsaved create operation and no GDN is reported as successfully saved. Confirmation behavior and whether form values are retained on return are unspecified.  
**Business Rule**: Cancel is shown in the screenshot; draft/cancel semantics are not documented.  
**Suggested Layer**: E2E

### TC-400: Exercise valid calendar boundary dates
**Category**: Edge Case  
**Priority**: P2  
**Preconditions**: Create form is open; the date input exposes a supported calendar range.  
**Steps**:
1. Identify the earliest and latest dates selectable in the date input, without assuming what the business range should be.
2. Attempt the create flow with each boundary date and otherwise valid required details.
**Expected Results**: Record whether each selectable boundary value is accepted and displayed consistently. The business-valid date range and any relationship between GDN Date and Ref Date are unspecified; obtain a business rule before asserting eligibility.  
**Business Rule**: The screenshot identifies date inputs but the domain document provides no GDN date limits.  
**Suggested Layer**: Component

### TC-401: Exercise empty and whitespace-only optional text
**Category**: Edge Case  
**Priority**: P2  
**Preconditions**: Create form is open; valid required details are available.  
**Steps**:
1. Leave Ref No. and Remarks empty and complete the form.
2. Repeat with whitespace-only values in those inputs if the controls permit it.
**Expected Results**: Empty optional fields do not block creation. Whitespace trimming/preservation is unspecified; observe and record whether whitespace is normalized or retained.  
**Business Rule**: Ref No. and Remarks are not marked required in the screenshot; no text normalization rule is documented.  
**Suggested Layer**: Component

### TC-402: Exercise long Ref No. and Remarks values
**Category**: Edge Case  
**Priority**: P2  
**Preconditions**: Create form is open; a safe test value longer than typical short text is available.  
**Steps**:
1. Enter long text in Ref No. and Remarks, staying within any visibly enforced control limit.
2. Complete the required fields and attempt to proceed/save.
3. Inspect the value in the resulting transaction if available.
**Expected Results**: The controls remain usable and the application either retains the entered text or clearly prevents a value beyond its supported limit. Maximum lengths and truncation policy are unspecified; do not assert a numeric limit without a documented rule.  
**Business Rule**: No Ref No. or Remarks length limits are documented.  
**Suggested Layer**: Component

### TC-403: Observe automatic numbering across repeated create attempts
**Category**: Edge Case  
**Priority**: P2  
**Preconditions**: User can open the create form more than once in a test environment.  
**Steps**:
1. Open the form and record the displayed AUTO GDN No.
2. Cancel without saving, reopen, and record the displayed number.
3. If permitted, save a valid GDN and observe the next create attempt's number.
**Expected Results**: Record when and how the system supplies a number. Whether cancelled attempts consume numbers, and the uniqueness/sequence policy, are unspecified; do not assert gaplessness or a particular format.  
**Business Rule**: GDN No. is labelled AUTO; sequencing behavior is not documented.  
**Suggested Layer**: E2E

### TC-500: Verify the initial Create screen and progress state
**Category**: UI State  
**Priority**: P1  
**Preconditions**: User can open Goods Dispatch Note - Create.  
**Steps**:
1. Open the create screen.
2. Inspect the title, Basic Details fields, Search control, footer actions, and progress indicator.
**Expected Results**: The title is **Goods Dispatch Note - Create**; Basic Details includes the screenshot-listed controls; footer shows Save and Cancel; progress displays step 2/4. Other step names and transitions are unspecified.  
**Business Rule**: These are visible UI states in the supplied screenshot.  
**Suggested Layer**: Component

### TC-501: Make required status clear in the UI
**Category**: UI State  
**Priority**: P1  
**Preconditions**: Create screen is open.  
**Steps**:
1. Inspect required indicators for GDN No., GDN Date, Location, Product, and Counterparty.
2. Compare Ref No., Ref Date, and Remarks.
**Expected Results**: Required status is apparent for the screenshot-required fields, with GDN No. identified as AUTO; the optional fields are not presented as required. Accessibility-specific indicator behavior is not specified.  
**Business Rule**: Required/optional status follows the supplied screenshot.  
**Suggested Layer**: Component

### TC-502: Verify dropdown loading and empty states
**Category**: UI State  
**Priority**: P2  
**Preconditions**: Create screen is open; a test environment can expose delayed or unavailable dropdown data without modifying production data.  
**Steps**:
1. Open Location, Product, and Counterparty dropdowns under normal conditions.
2. Repeat with the relevant master data unavailable or delayed, if controllable.
**Expected Results**: Users can distinguish loading, available choices, and no available choices and can recover without selecting an unintended value. Exact loading indicators, empty-state copy, and dependency between dropdowns are unspecified.  
**Business Rule**: The screenshot shows three required dropdowns; their loading and dependency behavior is not documented.  
**Suggested Layer**: Component

### TC-503: Reflect the Use Blocked Batches checkbox state
**Category**: UI State  
**Priority**: P2  
**Preconditions**: Create screen is open.  
**Steps**:
1. Observe the initial Use Blocked Batches checkbox state.
2. Toggle it on and off and observe the displayed state.
3. Search or navigate within the create flow if available, then observe whether the selection is retained.
**Expected Results**: The control visibly reflects user interaction. Default state, effect on batch eligibility, and retention across steps are unspecified and must not be assumed from the label alone.  
**Business Rule**: The screenshot includes the checkbox; the domain document does not define its effect.  
**Suggested Layer**: Component

### TC-504: Show actionable feedback for required-field errors
**Category**: UI State  
**Priority**: P2  
**Preconditions**: Create form is open.  
**Steps**:
1. Attempt Search/Save with one required field missing.
2. Observe field-level feedback, focus, and whether the user can correct the field without losing other entered values.
**Expected Results**: The user can identify and correct the missing required value; unrelated entered values are not silently lost. Exact message, focus behavior, and whether feedback appears on Search or Save are unspecified.  
**Business Rule**: The screenshot marks GDN Date, Location, Product, and Counterparty required.  
**Suggested Layer**: Component

### TC-505: Preserve or discard entered values consistently on Cancel
**Category**: UI State  
**Priority**: P2  
**Preconditions**: Create form is open with at least one value entered.  
**Steps**:
1. Select Cancel.
2. If the form remains open or a confirmation appears, observe the unsaved-value state.
3. Reopen the create screen.
**Expected Results**: The form exits or presents a clear decision about abandoning the draft. Whether values are retained after cancel/reopen is unspecified; verify that no saved transaction is falsely indicated.  
**Business Rule**: Cancel is present in the screenshot; its value-retention behavior is undocumented.  
**Suggested Layer**: Component
