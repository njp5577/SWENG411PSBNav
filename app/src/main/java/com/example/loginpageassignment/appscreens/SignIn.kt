package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.example.loginpageassignment.utilities.managers.DatabaseManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mindrot.jbcrypt.BCrypt

//controller for the sign in page
class SignIn : LoggedOutPage()
{
    private lateinit var buttonSignUp: Button
    private lateinit var buttonReset: Button

    //controls which type of user views which home page
    private fun navigateToHomePage(userType: String, username: String)
    {
        val go: Intent = when (userType)
        {
            "Admin" -> Intent(this, AdminHome::class.java)
            "EventOrg" -> Intent(this, Homepage::class.java)
            else -> Intent(this, Homepage::class.java)
        }

        val json = Json.encodeToString(CurrentUser(username))
        go.putExtra("User", json)
        startActivity(go)
    }

    //refreshes current page
    override fun refresh() { startActivity(Intent(this, SignIn::class.java)) }

    //on activity create
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //Show the login page
        setContentView(R.layout.activity_login)

        //Get info from text inputs and buttons
        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        setButtonLoginFun(findViewById(R.id.buttonLogin))
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonReset = findViewById(R.id.buttonForgot)

        val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

        //When user wants to reset password
        buttonReset.setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
        }

        //When user wants to sign up
        buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        //When user wants to login
        getButtonLoginFun().setOnClickListener{
            // Validate the username and password
            val username = getEditTextUsernameFun().text.toString()
            val password = getEditTextPasswordFun().text.toString()

            userRef?.whereEqualTo("username", username)?.get()
                ?.addOnSuccessListener{ documents ->
                    val hashed = documents.documents[0].getString("password")

                    //Check that account exists and validate the password
                    if((documents.isEmpty || !BCrypt.checkpw(password, hashed)))
                        showToast("Invalid username or password.", this)
                    else //if account exists, verify password
                        navigateToHomePage(documents.documents[0].getString("type")!!, username)
            }
        }
    }
}