
# Event

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**address** | **String** | Address of the event venue |  [optional]
**district** | [**District**](District.md) |  |  [optional]
**endDate** | [**Date**](Date.md) | When the event ends |  [optional]
**eventCode** | **String** | Event short code, as provided by FIRST | 
**eventType** | **Integer** | An integer that represents the event type as a constant. |  [optional]
**eventTypeString** | **String** | A human readable string that defines the event type. |  [optional]
**gmapsUrl** | **String** | URL for the venue on Google Maps |  [optional]
**key** | **String** | TBA event key with the format yyyy[EVENT_CODE], where yyyy is the year, and EVENT_CODE is the event code of the event. | 
**lastModified** | **Long** | Timestamp this model was last modified |  [optional]
**locationName** | **String** | Short name of the venue |  [optional]
**name** | **String** | Official name of event on record either provided by FIRST or organizers of offseason event. | 
**shortName** | **String** | Same as name but doesn&#39;t include event specifiers, such as &#39;Regional&#39; or &#39;District&#39;. May be null. |  [optional]
**startDate** | [**Date**](Date.md) | When the event starts |  [optional]
**timezone** | **String** | Timezone name |  [optional]
**webcasts** | **String** | If the event has webcast data associated with it, this contains JSON data of the streams |  [optional]
**website** | **String** | The event&#39;s website, if any. |  [optional]
**week** | **Integer** | Week of the season the event occurs on |  [optional]
**year** | **Integer** | Year the event data is for. | 



