
@XBS_Commodity_Contract_Management
Feature: Commodity Contract Management
  As a Contract Manager.I want to create a Commodity Contract with only the mandatory fields
  So that I can quickly onboard a new supplier agreement without unnecessary data entry.

  @XBS_Commodity_Contract_Management_Flow @smoke @regression
  Scenario: Create Commodity Contract Management end to end flow with only mandatory fields

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
    Then verify "Rolling""calendar" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Force Close""times" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Toggle Columns""bi bi-list-check" boot strap icon is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "Filter""filter" prime icons is displayed on the Commercial Commodity Contract Management dashboard
    Then verify "More""angle-down" prime icons is displayed on the Commercial Commodity Contract Management dashboard

    ## Commodity Contract Management
    When clicks on "Create" "plus" icon on the Commodity Contract Management dashboard
    Then verifies that the "Commodity Contract Management - Create General" popup is displayed

    Then verifies that the "General" section is displayed
    Then verifies that the "Delivery Terms" section is displayed
    Then verifies that the "Documents" section is displayed
    Then verifies that the "Remarks and Instructions" section is displayed

    And select "Class" dropdown value as "Coffee Farmer Purchase Contract"


