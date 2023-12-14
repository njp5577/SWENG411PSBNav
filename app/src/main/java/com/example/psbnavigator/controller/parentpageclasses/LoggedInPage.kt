package com.example.psbnavigator.controller.parentpageclasses

import android.content.Intent
import android.view.MenuItem
import android.widget.Button
import com.example.psbnavigator.R
import com.example.psbnavigator.controller.appscreens.Homepage
import com.example.psbnavigator.controller.appscreens.MapPage
import com.example.psbnavigator.model.dataobjects.CurrentUser
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