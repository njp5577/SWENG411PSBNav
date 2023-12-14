package com.example.psbnavigator.controller.appscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageAdmin
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminAddEventOrg : LoggedInPageAdmin()
{
    private lateinit var editTextEmail: EditText
    private lateinit var buttonEventOrg: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

    override fun refresh()
    {
        val go = Intent(this, AdminAddEventOrg::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminaddeventorg)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonEventOrg.setOnClickListener { handleEventOrg() }
    }

    private fun initializeView()
    {
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonEventOrg = findViewById(R.id.buttonEventOrg)
    }

    private fun handleEventOrg()
    {
        val iemail = editTextEmail.text.toString()

        //Check all users to look for a match
        userRef?.whereEqualTo("email", iemail)?.get()?.addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty)
            {
                showToast("No account under that email.", this)
            }
            else
            {
                changeToEventOrg(documents)
            }
        }
    }

    private fun changeToEventOrg(documents : QuerySnapshot)
    {
        documents.documents[0].reference.update("type", "EventOrg").addOnSuccessListener {
            showToast("The user under this email has been turned into an event organizer.", this)
            refresh()
        }
    }
}