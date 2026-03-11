Feature: Amazon Product Search
  As a user
  I want to search for products on Amazon
  So that I can view the search results

  Background:
    Given I open the Amazon website

  @smoke @search
  Scenario: Search for iPhone 17 Pro on Amazon
    When I search for the product "iphone 17 pro"
    Then I should see search results for "iphone 17 pro"
