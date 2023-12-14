//PSB_Event.java
package com.example.psbnavigator.model.dataobjects;
import com.google.firebase.firestore.PropertyName;

//Holds data for a single Penn State Behrend event
public class PSB_Event
{
    @PropertyName("Event Creator")
    private String eventCreator;
    @PropertyName("Name")
    private String eventName;

    @PropertyName("Location")
    private String eventLocation;

    @PropertyName("Time")
    private String eventTime;

    @PropertyName("Date")
    private String eventDate;

    @PropertyName("Description")
    private String eventDescription;

    public PSB_Event()
    {
        eventCreator = "Sample Event Creator";
        eventName = "Sample Event Name";
        eventLocation= "Sample Event Location";
        eventTime = "Sample Event Time";
        eventDate = "Sample Event Date";
        eventDescription = "Sample Event Description";
    }

    @SuppressWarnings("unused")
    public PSB_Event(String ec)
    {
        eventCreator = ec;
        eventName = "Sample Event Name";
        eventLocation= "Sample Event Location";
        eventTime = "Sample Event Time";
        eventDate = "Sample Event Date";
        eventDescription = "Sample Event Description";
    }

    @SuppressWarnings("unused")
    public PSB_Event(String ec, String n, String l, String t, String da, String de)
    {
        eventCreator = ec;
        eventName = n;
        eventLocation = l;
        eventTime = t;
        eventDate = da;
        eventDescription = de;
    }

    @PropertyName("Event Creator")
    public String getEventCreator()
    {
        return this.eventCreator;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Event Creator")
    public void setEventCreator(String eventCreator)
    {
        this.eventCreator = eventCreator;
    }
    @PropertyName("Name")
    public String getEventName()
    {
        return this.eventName;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Name")
    public void setEventName(String name)
    {
        this.eventName = name;
    }

    @PropertyName("Location")
    public String getEventLocation()
    {
        return this.eventLocation;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Location")
    public void setEventLocation(String eventLocation)
    {
        this.eventLocation = eventLocation;
    }

    @PropertyName("Time")
    public String getEventTime()
    {
        return this.eventTime;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Time")
    public void setEventTime(String eventTime)
    {
        this.eventTime = eventTime;
    }

    @PropertyName("Date")
    public String getEventDate()
    {
        return this.eventDate;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Date")
    public void setEventDate(String eventDate)
    {
        this.eventDate = eventDate;
    }

    @PropertyName("Description")
    public String getEventDescription()
    {
        return eventDescription;
    }

    @SuppressWarnings("unused") //remove when used
    @PropertyName("Description")
    public void setEventDescription(String eventDescription)
    {
        this.eventDescription = eventDescription;
    }
}