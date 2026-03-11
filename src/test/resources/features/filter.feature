Feature: Amazon Product Price Sorting
  As a user
  I want to sort products by Price Low to High
  So that I can verify the sorting is correct

  Background:
    Given I open the Amazon website

  @regression @sorting
  Scenario: Verify products are sorted by Price Low to High
    When I search for the product "iphone 17 pro"
    And I click on the Sort By dropdown
    And I select "Price: Low to High" from the sort options
    Then the first 3 product prices should be sorted in ascending order
