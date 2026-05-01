@MS_Search @SubComp_Search::Service @Module_Hybrid::Search::Relationship @author_pdewanga @assignee_pdewanga @reporter_pdewanga
Feature: Hybrid::Search API validation for indirect relationships
  I want to use this feature file to test
  * Direct and indirect relationships
  * Full path traversal
  * Bi-directional relationships
  * Filter on asset/relationship attributes

  Background:
    * def tenant = callonce read('classpath:reusable/utility.feature@SessionId')
    * string orgId = tenant.response.orgId
    * json roles = tenant.response.effectiveRoles
    * string sessionId = tenant.response.sessionId
    * string userId = tenant.response.id
    * def authToken = callonce read('classpath:reusable/utility.feature@JWTToken') {sessionID : '#(sessionId)'}
    * string jwtToken = authToken.response.jwt_token
    * header Authorization = 'Bearer ' + jwtToken
    * header X-INFA-ORG-ID = orgId
    * header X-INFA-TG-ID = TGID
    * header X-INFA-SEARCH-LANGUAGE = 'gremlin'


  @MS_Search @Module_Hybrid::Search::Relationship @PR_P1 @Epic_MDP-360 @Story_MDP-598 @testCategory_Functional @testSubCategory_Functional @type_Regression @jiraKey_CDGC-53270
Scenario: Verify the new field in graph which will store core.description in lower case and will be used for case insensitive search with simple data @jiraKey_CDGC-53270

  @MS_Search @Module_Hybrid::Search::Relationship @PR_P1 @Epic_MDP-360 @Story_MDP-598 @testCategory_Functional @testSubCategory_Functional @type_Regression @jiraKey_CDGC-53271
Scenario: Verify the new field in graph which will store core.description in lower case and will be used for case insensitive search with mixed data @jiraKey_CDGC-53271
