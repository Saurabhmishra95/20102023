
Feature: Portal /portal API Endpoint

  Background:
    Given AccessOne Scim server is available


  @CIAM-576
  @Ignore
  Scenario: Retrieve application details using /myapplications endpoint with a valid token
    Given I have a valid AccessOne user token
    And I access the portal/applicationdetails Resource
    Then the http status code is 200
    And the response has the field section with a value of "myApps"
    And the response has an array apps
    And the apps array has an entry with the field appId with a value of "c49c48c7-271b-48cc-a8a1-f470d0b26766"
    And the apps array has an entry with the field appName with a value of "eCare NEXT®"
    And the apps array has an entry with the field appDescription with a value of "eCare NEXT® is a central rules engine that provides intelligent automation to drive exception-based workflows."
    And the apps array has an entry with the field appIcon with a value of "blue"
    And the apps array has an entry with the field appUrl with a value of "https://www.ecarenext.com/eCareNext"
    And the apps array has an entry with the field appId with a value of "8b8e619a-a0d0-4335-a27e-6bada1f72e8a"
    And the apps array has an entry with the field appName with a value of "OneSource"
    And the apps array has an entry with the field appDescription with a value of "OneSource is a standalone web-based portal that provides access to a range of patient access services."
    And the apps array has an entry with the field appIcon with a value of "green"
    And the apps array has an entry with the field appUrl with a value of "https://onesource.passporthealth.com/"
    
    
    @CIAM-576
    Scenario: Retrieve organization details using /organizations endpoint
    Given I have a valid AccessOne user token
    When I access the portal/organizations Resource
    Then the http status code is 200
    And the response is a JSON array with one organization
    And the organization has the following details:
      | id                                  | name                            | description                       | address |
      | 35a7a66a-2d10-47e4-b474-5c248d1db49b | TestOrganisation_Acceptance_test | TestOrganisation_Acceptance_test  | {}      |
     
    @CIAM-579  
    Scenario: Fetch Organizations and Verify the Response
    Given I have a valid AccessOne user token
    And I access the portal/applicationdetails Resource
    Then the http status code is 200
    Then the response should have a JSON array
    And the response should have 4 elements in the array
    And each element in the array should have the following fields:
      | id                                  | name                               | description                       | address                            |
      | "07e39148-dd23-4dd5-ab21-1966a8361658" | "Mikes Parent Organization" | "Test for the Organizational Hierarchy" | { "streetAddress": "1234 Hospital Lane" } |
      | "c29327cb-ee44-48ae-9cd1-fbc3f9f6de38" | "Mike Department"           | null                                 | null                                |
      | "dae1831c-9810-4187-9289-7309b2cab9fd" | "Medical Present Value, Inc." | "Test for the Organizational Hierarchy" | null                                |
      | "35a7a66a-2d10-47e4-b474-5c248d1db49b" | "TestOrganisation_Acceptance_test" | "TestOrganisation_Acceptance_test" | {}                                  |





    







