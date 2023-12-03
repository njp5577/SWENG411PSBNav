package com.example.loginpageassignment.utilities.queue;

import com.example.loginpageassignment.dataobjects.Location;

public class QueueManager
{
    private String username;
    public QueueManager(String u)
    {
        username = u;
    }

    public boolean addToQueue(Location location)
    {
        //TODO: Write function
        return false;
    }

    public boolean removeFromQueue(Location location)
    {
        //TODO: Write function
        return false;
    }

    //shift up/down by +/- 1
    public boolean reorderQueue(int shift, int position)
    {
        //TODO: Write function
        return false;
    }

    public int searchForElement(Location location)
    {
        //TODO: Write function
        return -1;
    }
}
