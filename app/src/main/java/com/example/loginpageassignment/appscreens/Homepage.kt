package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedInPageUser
import com.example.loginpageassignment.utilities.managers.DatabaseManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Homepage() : LoggedInPageUser()
{
    private lateinit var buttonMapPage: Button
    private lateinit var buttonDestinationQueue: Button
    private lateinit var buttonEventPage: Button
    private lateinit var buttonManageEvents: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

    private fun getButtonMapPageFun() : Button { return this.buttonMapPage }

    private fun setButtonMapPageFun(buttonMapPage: Button){ this.buttonMapPage = buttonMapPage }

    private fun getButtonDestinationQueueFun() : Button { return this.buttonDestinationQueue }

    private fun setButtonDestinationQueueFun(buttonDestinationQueue: Button){ this.buttonDestinationQueue = buttonDestinationQueue }

    private fun getButtonEventPageFun() : Button { return this.buttonEventPage }

    private fun setButtonEventPageFun(buttonEventPage: Button){ this.buttonEventPage = buttonEventPage }

    override fun refresh()
    {
        val go = Intent(this, Homepage::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setButtonMapPageFun(findViewById(R.id.buttonMapPage))
        setButtonDestinationQueueFun(findViewById(R.id.buttonDestinationQueue))
        setButtonEventPageFun(findViewById(R.id.buttonEventPage))
        buttonManageEvents = findViewById(R.id.buttonManageEvents)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        showToast("Hello ${getLoggedInAsFun().username}!", this)

        getButtonMapPageFun().setOnClickListener {
            val go = Intent(this, MapPage::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        getButtonDestinationQueueFun().setOnClickListener {
            val go = Intent(this, DestinationQueue::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        getButtonEventPageFun().setOnClickListener {
            val go = Intent(this, EventPage::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonManageEvents.setOnClickListener {
            userRef?.whereEqualTo("username", getLoggedInAsFun().username)?.get()
                ?.addOnSuccessListener { documents ->
                    if(documents.documents[0].getString("type") == "EventOrg")
                    {
                        val go = Intent(this, EventOrgManageEvents::class.java)
                        val json = Json.encodeToString(getLoggedInAsFun())
                        go.putExtra("User", json)
                        startActivity(go)
                    }
                    else
                    {
                        showToast("You must be an event organizer to access this page.", this)
                    }
                }
        }
    }
}