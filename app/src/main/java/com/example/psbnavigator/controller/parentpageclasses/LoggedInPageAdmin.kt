package com.example.psbnavigator.controller.parentpageclasses

import android.content.Intent
import android.view.MenuItem
import com.example.psbnavigator.R
import com.example.psbnavigator.controller.appscreens.AdminHome
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class LoggedInPageAdmin : LoggedInPage()
{
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.home -> {
                val go = Intent(this, AdminHome::class.java)
                val json = Json.encodeToString(getLoggedInAsFun())
                go.putExtra("User", json)
                startActivity(go)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}