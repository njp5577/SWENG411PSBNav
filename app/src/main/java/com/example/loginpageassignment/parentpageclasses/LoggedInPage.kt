package com.example.loginpageassignment.parentpageclasses

import android.widget.Button
import com.example.loginpageassignment.dataobjects.CurrentUser
import kotlin.system.exitProcess

abstract class LoggedInPage : Page() {

    private lateinit var loggedInAs: CurrentUser

    protected lateinit var buttonLogOut: Button

    protected fun getLoggedInAsFun(): CurrentUser {
        return this.loggedInAs
    }

    protected fun setLoggedInAsFun(loggedInAs: CurrentUser){
        this.loggedInAs = loggedInAs
    }

    protected fun getButtonLogOutFun(): Button{
        return this.buttonLogOut
    }

    protected fun setButtonLogOutFun(buttonLogOut: Button){
        this.buttonLogOut = buttonLogOut
    }

    protected fun logOut(){
        finish()
        exitProcess(0)
    }
}