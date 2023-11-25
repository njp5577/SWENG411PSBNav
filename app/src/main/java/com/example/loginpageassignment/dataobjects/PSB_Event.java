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

    @PropertyName("Name")
    public void setEventName(String name) {
        this.eventName = name;
    }

    @PropertyName("Location")
    public String getEventLocation() {
        return this.eventLocation;
    }

    @PropertyName("Location")
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    @PropertyName("Time")
    public String getEventTime() {
        return this.eventTime;
    }

    @PropertyName("Time")
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    @PropertyName("Date")
    public String getEventDate() {
        return this.eventDate;
    }

    @PropertyName("Date")
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
