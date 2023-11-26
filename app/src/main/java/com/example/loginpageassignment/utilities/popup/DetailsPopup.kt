package com.example.loginpageassignment.utilities.popup

import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.PSB_Event

abstract class DetailsPopup()
{
    //override show details for each new parameter type to display
    abstract fun showDetails(event: PSB_Event, user: CurrentUser)
}