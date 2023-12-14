package com.example.psbnavigator.controller.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageUser
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//controller for the home page
class Homepage() : LoggedInPageUser()
{
    private lateinit var buttonMapPage: Button
    private lateinit var buttonDestinationQueue: Button
    private lateinit var buttonEventPage: Button
    private lateinit var buttonManageEvents: Button
    private lateinit var buttonLogout: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

    //refreshes the current page
    override fun refresh()
    {
        val go = Intent(this, Homepage::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    //on activity create
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //initialize vars for buttons
        buttonMapPage = findViewById(R.id.buttonMapPage)
        buttonDestinationQueue = findViewById(R.id.buttonDestinationQueue)
        buttonEventPage = findViewById(R.id.buttonEventPage)
        buttonManageEvents = findViewById(R.id.buttonManageEvents)
        buttonLogout = findViewById(R.id.buttonLogout)

        //set logged in user
        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        showToast("Hello ${getLoggedInAsFun().username}!", this)

        //set listeners
        buttonMapPage.setOnClickListener {
            val go = Intent(this, MapPage::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonDestinationQueue.setOnClickListener {
            val go = Intent(this, DestinationQueue::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonEventPage.setOnClickListener {
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

        buttonLogout.setOnClickListener{
            val go = Intent(this, SignIn::class.java)
            startActivity(go)

        }
    }
}