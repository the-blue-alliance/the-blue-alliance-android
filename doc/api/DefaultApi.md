# DefaultApi

All URIs are relative to *http://www.thebluealliance.com/api/v3*

Method | HTTP request | Description
------------- | ------------- | -------------
[**fetchApiStatus**](DefaultApi.md#fetchApiStatus) | **GET** api/v3/status | API Status Request
[**fetchDistrictEvents**](DefaultApi.md#fetchDistrictEvents) | **GET** api/v3/district/{district_key}/events | District Events Request
[**fetchDistrictList**](DefaultApi.md#fetchDistrictList) | **GET** api/v3/districts/{year} | District List Request
[**fetchDistrictRankings**](DefaultApi.md#fetchDistrictRankings) | **GET** api/v3/district/{district_key}/rankings | District Rankings Reques
[**fetchDistrictTeamsInYear**](DefaultApi.md#fetchDistrictTeamsInYear) | **GET** api/v3/district/{district_key}/teams | District Teams Request
[**fetchEvent**](DefaultApi.md#fetchEvent) | **GET** api/v3/event/{event_key} | Event Info Request
[**fetchEventAlliances**](DefaultApi.md#fetchEventAlliances) | **GET** api/v3/event/{event_key}/alliances | Event Alliances Request
[**fetchEventAwards**](DefaultApi.md#fetchEventAwards) | **GET** api/v3/event/{event_key}/awards | Event Awards Request
[**fetchEventDistrictPoints**](DefaultApi.md#fetchEventDistrictPoints) | **GET** api/v3/event/{event_key}/district_points | Event District Points Request
[**fetchEventMatches**](DefaultApi.md#fetchEventMatches) | **GET** api/v3/event/{event_key}/matches | Event Matches Request
[**fetchEventOPR**](DefaultApi.md#fetchEventOPR) | **GET** api/v3/event/{event_key}/oprs | Event OPR Request
[**fetchEventRankings**](DefaultApi.md#fetchEventRankings) | **GET** api/v3/event/{event_key}/rankings | Event Rankings Request
[**fetchEventTeams**](DefaultApi.md#fetchEventTeams) | **GET** api/v3/event/{event_key}/teams | Event Teams Request
[**fetchEventsInYear**](DefaultApi.md#fetchEventsInYear) | **GET** api/v3/events/{year} | Event List Request
[**fetchMatch**](DefaultApi.md#fetchMatch) | **GET** api/v3/match/{match_key} | Match Request
[**fetchTeam**](DefaultApi.md#fetchTeam) | **GET** api/v3/team/{team_key} | Single Team Request
[**fetchTeamAtEventAwards**](DefaultApi.md#fetchTeamAtEventAwards) | **GET** api/v3/team/{team_key}/event/{event_key}/awards | Team Event Awards Request
[**fetchTeamAtEventMatches**](DefaultApi.md#fetchTeamAtEventMatches) | **GET** api/v3/team/{team_key}/event/{event_key}/matches | Team Event Matches Request
[**fetchTeamDistricts**](DefaultApi.md#fetchTeamDistricts) | **GET** api/v3/team/{team_key}/districts | Team Districts Request
[**fetchTeamEvents**](DefaultApi.md#fetchTeamEvents) | **GET** api/v3/team/{team_key}/{year}/events | Team Events Request
[**fetchTeamMediaInYear**](DefaultApi.md#fetchTeamMediaInYear) | **GET** api/v3/team/{team_key}/media/{year} | Team Media Request
[**fetchTeamPage**](DefaultApi.md#fetchTeamPage) | **GET** api/v3/teams/{page} | Team List Request
[**fetchTeamRobots**](DefaultApi.md#fetchTeamRobots) | **GET** api/v3/team/{team_key}/robots | Team Robots Request
[**fetchTeamSocialMedia**](DefaultApi.md#fetchTeamSocialMedia) | **GET** api/v3/team/{team_key}/social_media | Team Social Media Request
[**fetchTeamYearsParticipated**](DefaultApi.md#fetchTeamYearsParticipated) | **GET** api/v3/team/{team_key}/years_participated | Team Years Participated Request


<a name="fetchApiStatus"></a>
# **fetchApiStatus**
> ApiStatus fetchApiStatus()

API Status Request

Get various metadata about the TBA API

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
try {
    ApiStatus result = apiInstance.fetchApiStatus();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchApiStatus");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ApiStatus**](ApiStatus.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchDistrictEvents"></a>
# **fetchDistrictEvents**
> List&lt;Event&gt; fetchDistrictEvents(districtKey, xTBACache)

District Events Request

Fetch a list of events within a given district

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String districtKey = "districtKey_example"; // String | Key identifying a district (e.g. '2016ne')
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Event> result = apiInstance.fetchDistrictEvents(districtKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchDistrictEvents");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **districtKey** | **String**| Key identifying a district (e.g. &#39;2016ne&#39;) |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Event&gt;**](Event.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchDistrictList"></a>
# **fetchDistrictList**
> List&lt;District&gt; fetchDistrictList(year, xTBACache)

District List Request

Fetch a list of active districts in the given year

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
Integer year = 56; // Integer | A specific year to request data for.
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<District> result = apiInstance.fetchDistrictList(year, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchDistrictList");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **year** | **Integer**| A specific year to request data for. |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;District&gt;**](District.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchDistrictRankings"></a>
# **fetchDistrictRankings**
> List&lt;DistrictRanking&gt; fetchDistrictRankings(districtKey, xTBACache)

District Rankings Reques

Fetch district rankings

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String districtKey = "districtKey_example"; // String | Key identifying a district (e.g. '2016ne')
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<DistrictRanking> result = apiInstance.fetchDistrictRankings(districtKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchDistrictRankings");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **districtKey** | **String**| Key identifying a district (e.g. &#39;2016ne&#39;) |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;DistrictRanking&gt;**](DistrictRanking.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchDistrictTeamsInYear"></a>
# **fetchDistrictTeamsInYear**
> List&lt;Team&gt; fetchDistrictTeamsInYear(districtKey, xTBACache)

District Teams Request

Fetch a list of teams within a given district

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String districtKey = "districtKey_example"; // String | Key identifying a district (e.g. '2016ne')
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Team> result = apiInstance.fetchDistrictTeamsInYear(districtKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchDistrictTeamsInYear");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **districtKey** | **String**| Key identifying a district (e.g. &#39;2016ne&#39;) |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Team&gt;**](Team.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEvent"></a>
# **fetchEvent**
> Event fetchEvent(eventKey, xTBACache)

Event Info Request

Fetch details for one event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    Event result = apiInstance.fetchEvent(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEvent");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**Event**](Event.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventAlliances"></a>
# **fetchEventAlliances**
> List&lt;EventAlliance&gt; fetchEventAlliances(eventKey, xTBACache)

Event Alliances Request

Fetch alliance information for one event.

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<EventAlliance> result = apiInstance.fetchEventAlliances(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventAlliances");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;EventAlliance&gt;**](EventAlliance.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventAwards"></a>
# **fetchEventAwards**
> List&lt;Award&gt; fetchEventAwards(eventKey, xTBACache)

Event Awards Request

Fetch awards for the given event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Award> result = apiInstance.fetchEventAwards(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventAwards");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Award&gt;**](Award.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventDistrictPoints"></a>
# **fetchEventDistrictPoints**
> String fetchEventDistrictPoints(eventKey, xTBACache)

Event District Points Request

Fetch district points for one event.

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    String result = apiInstance.fetchEventDistrictPoints(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventDistrictPoints");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventMatches"></a>
# **fetchEventMatches**
> List&lt;Match&gt; fetchEventMatches(eventKey, xTBACache)

Event Matches Request

Fetch matches for the given event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Match> result = apiInstance.fetchEventMatches(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventMatches");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Match&gt;**](Match.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventOPR"></a>
# **fetchEventOPR**
> String fetchEventOPR(eventKey, xTBACache)

Event OPR Request

Fetch OPR details for one event.

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    String result = apiInstance.fetchEventOPR(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventOPR");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventRankings"></a>
# **fetchEventRankings**
> RankingResponseObject fetchEventRankings(eventKey, xTBACache)

Event Rankings Request

Fetch ranking details for one event.

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    RankingResponseObject result = apiInstance.fetchEventRankings(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventRankings");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**RankingResponseObject**](RankingResponseObject.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventTeams"></a>
# **fetchEventTeams**
> List&lt;Team&gt; fetchEventTeams(eventKey, xTBACache)

Event Teams Request

Fetch teams attending the given event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Team> result = apiInstance.fetchEventTeams(eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventTeams");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Team&gt;**](Team.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchEventsInYear"></a>
# **fetchEventsInYear**
> List&lt;Event&gt; fetchEventsInYear(year, xTBACache)

Event List Request

Fetch all events in a year

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
Integer year = 56; // Integer | A specific year to request data for.
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Event> result = apiInstance.fetchEventsInYear(year, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchEventsInYear");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **year** | **Integer**| A specific year to request data for. |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Event&gt;**](Event.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchMatch"></a>
# **fetchMatch**
> Match fetchMatch(matchKey, xTBACache)

Match Request

Fetch details about a single match

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String matchKey = "matchKey_example"; // String | Key identifying a single match, has format [event key]_[match id]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    Match result = apiInstance.fetchMatch(matchKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchMatch");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **matchKey** | **String**| Key identifying a single match, has format [event key]_[match id] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**Match**](Match.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeam"></a>
# **fetchTeam**
> Team fetchTeam(teamKey, xTBACache)

Single Team Request

This endpoit returns information about a single team

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    Team result = apiInstance.fetchTeam(teamKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeam");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**Team**](Team.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamAtEventAwards"></a>
# **fetchTeamAtEventAwards**
> List&lt;Award&gt; fetchTeamAtEventAwards(teamKey, eventKey, xTBACache)

Team Event Awards Request

Fetch all awards won by a single team at an event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Award> result = apiInstance.fetchTeamAtEventAwards(teamKey, eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamAtEventAwards");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Award&gt;**](Award.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamAtEventMatches"></a>
# **fetchTeamAtEventMatches**
> List&lt;Match&gt; fetchTeamAtEventMatches(teamKey, eventKey, xTBACache)

Team Event Matches Request

Fetch all matches for a single team at an event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String eventKey = "eventKey_example"; // String | Key identifying a single event, has format [year][event code]
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Match> result = apiInstance.fetchTeamAtEventMatches(teamKey, eventKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamAtEventMatches");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **eventKey** | **String**| Key identifying a single event, has format [year][event code] |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Match&gt;**](Match.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamDistricts"></a>
# **fetchTeamDistricts**
> List&lt;String&gt; fetchTeamDistricts(teamKey, xTBACache)

Team Districts Request

Fetch all district keys that a team has competed in

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<String> result = apiInstance.fetchTeamDistricts(teamKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamDistricts");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

**List&lt;String&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamEvents"></a>
# **fetchTeamEvents**
> List&lt;Event&gt; fetchTeamEvents(teamKey, year, xTBACache)

Team Events Request

Fetch all events for a given team in a given year

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
Integer year = 56; // Integer | A specific year to request data for.
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Event> result = apiInstance.fetchTeamEvents(teamKey, year, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamEvents");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **year** | **Integer**| A specific year to request data for. |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Event&gt;**](Event.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamMediaInYear"></a>
# **fetchTeamMediaInYear**
> List&lt;Media&gt; fetchTeamMediaInYear(teamKey, year, xTBACache)

Team Media Request

Fetch media associated with a team in a given year

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
Integer year = 56; // Integer | A specific year to request data for.
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Media> result = apiInstance.fetchTeamMediaInYear(teamKey, year, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamMediaInYear");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **year** | **Integer**| A specific year to request data for. |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Media&gt;**](Media.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamPage"></a>
# **fetchTeamPage**
> List&lt;Team&gt; fetchTeamPage(page, xTBACache)

Team List Request

Returns a page containing 500 teams

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
Integer page = 56; // Integer | A page of teams, zero-indexed. Each page consists of teams whose numbers start at start = 500 * page_num and end at end = start + 499, inclusive.
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Team> result = apiInstance.fetchTeamPage(page, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamPage");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page** | **Integer**| A page of teams, zero-indexed. Each page consists of teams whose numbers start at start &#x3D; 500 * page_num and end at end &#x3D; start + 499, inclusive. |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Team&gt;**](Team.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamRobots"></a>
# **fetchTeamRobots**
> List&lt;Robot&gt; fetchTeamRobots(teamKey, xTBACache)

Team Robots Request

Fetch all robots a team has made since 2015. Robot names are scraped from TIMS.

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Robot> result = apiInstance.fetchTeamRobots(teamKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamRobots");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Robot&gt;**](Robot.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamSocialMedia"></a>
# **fetchTeamSocialMedia**
> List&lt;Media&gt; fetchTeamSocialMedia(teamKey, xTBACache)

Team Social Media Request

Fetch social media profiles for a team

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Media> result = apiInstance.fetchTeamSocialMedia(teamKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamSocialMedia");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

[**List&lt;Media&gt;**](Media.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="fetchTeamYearsParticipated"></a>
# **fetchTeamYearsParticipated**
> List&lt;Integer&gt; fetchTeamYearsParticipated(teamKey, xTBACache)

Team Years Participated Request

Fetch the years for which the team was registered for an event

### Example
```java
// Import classes:
//import com.thebluealliance.api.ApiException;
//import com.thebluealliance.api.call.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String teamKey = "teamKey_example"; // String | Key identifying a single team, has format frcXXXX, where XXXX is the team number
String xTBACache = "xTBACache_example"; // String | Special TBA App Internal Header to indicate caching strategy.
try {
    List<Integer> result = apiInstance.fetchTeamYearsParticipated(teamKey, xTBACache);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#fetchTeamYearsParticipated");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **teamKey** | **String**| Key identifying a single team, has format frcXXXX, where XXXX is the team number |
 **xTBACache** | **String**| Special TBA App Internal Header to indicate caching strategy. | [optional]

### Return type

**List&lt;Integer&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

