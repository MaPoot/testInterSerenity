Feature: Test

  Scenario: Testing pokeAPI
    Given I open the pokeApi Page
    Then Send text "pokemon/pikachu"
    Then Click submit
    Then Just wait 9000000