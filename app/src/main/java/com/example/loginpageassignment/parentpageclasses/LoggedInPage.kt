package com.example.loginpageassignment.parentpageclasses

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.example.loginpageassignment.R
import com.example.loginpageassignment.appscreens.Homepage
import com.example.loginpageassignment.dataobjects.CurrentUser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

abstract class LoggedInPage : Page()
{

    private lateinit var loggedInAs: CurrentUser
    private lateinit var buttonLogOut: Button

    protected fun getLoggedInAsFun(): CurrentUser {
        return this.loggedInAs
    }

    protected fun setLoggedInAsFun(loggedInAs: CurrentUser){ this.loggedInAs = loggedInAs }

    protected fun getButtonLogOutFun(): Button{ return this.buttonLogOut }

    protected fun setButtonLogOutFun(buttonLogOut: Button){ this.buttonLogOut = buttonLogOut }

    protected fun logOut()
    {
        finish()
        exitProcess(0)
    }

    abstract override fun refresh()

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.home -> {
                val go = Intent(this, Homepage::class.java)
                val json = Json.encodeToString(getLoggedInAsFun())
                go.putExtra("User", json)
                startActivity(go)
                true
            }
            R.id.refresh -> {
                refresh()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}