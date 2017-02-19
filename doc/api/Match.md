
# Match

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**actualTime** | **Long** | UNIX timestamp of when the match actually started |  [optional]
**alliances** | [**MatchAlliancesContainer**](MatchAlliancesContainer.md) |  |  [optional]
**compLevel** | **String** | The competition level the match was played at. | 
**eventKey** | **String** | Event key of the event the match was played at. | 
**key** | **String** | TBA event key with the format yyyy[EVENT_CODE]_[COMP_LEVEL]m[MATCH_NUMBER], where yyyy is the year, and EVENT_CODE is the event code of the event, COMP_LEVEL is (qm, ef, qf, sf, f), and MATCH_NUMBER is the match number in the competition level. A set number may append the competition level if more than one match in required per set . | 
**lastModified** | **Long** | Timestamp this model was last modified |  [optional]
**matchNumber** | **Integer** | The match number of the match in the competition level. | 
**scoreBreakdown** | **String** | Score breakdown for auto, teleop, etc. points. Varies from year to year. May be null. |  [optional]
**setNumber** | **Integer** | The set number in a series of matches where more than one match is required in the match series. | 
**time** | **Long** | UNIX timestamp of match time, as taken from the published schedule |  [optional]
**videos** | [**List&lt;MatchVideo&gt;**](MatchVideo.md) |  |  [optional]
**winningAlliance** | **String** | Which alliance won |  [optional]



