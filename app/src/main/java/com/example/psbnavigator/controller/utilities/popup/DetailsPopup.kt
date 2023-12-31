package com.example.psbnavigator.controller.utilities.popup

import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.model.dataobjects.PSB_Event

abstract class DetailsPopup()
{
    //override show details for each new parameter type to display
    abstract fun showDetails(event: PSB_Event, user: CurrentUser)
}