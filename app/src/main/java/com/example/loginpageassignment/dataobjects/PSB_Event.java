//PSB_Event.java
package com.example.loginpageassignment.dataobjects;
import com.google.firebase.firestore.PropertyName;

/**
 * Holds data for a single Penn State Behrend event
 */
public class PSB_Event
{
    @PropertyName("Name")
    private String eventName;

    @PropertyName("Location")
    private String eventLocation;

    @PropertyName("Time")
    private String eventTime;

    @PropertyName("Date")
    private String eventDate;

    @SuppressWarnings("unused")
    public PSB_Event()
    {
        eventName = "Sample Event Name";
        eventLocation= "Sample Event Location";
        eventTime = "Sample Event Time";
        eventDate = "Sample Event Date";
    }

    @SuppressWarnings("unused")
    public PSB_Event(String n, String l, String t, String d)
    {
        eventName = n;
        eventLocation = l;
        eventTime = t;
        eventDate = d;
    }

    @PropertyName("Name")
    public String getEventName() {
        return this.eventName;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Name")
    public void setEventName(String name) {
        this.eventName = name;
    }

    @PropertyName("Location")
    public String getEventLocation() {
        return this.eventLocation;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Location")
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    @PropertyName("Time")
    public String getEventTime() {
        return this.eventTime;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Time")
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    @PropertyName("Date")
    public String getEventDate() {
        return this.eventDate;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Date")
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
