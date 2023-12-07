package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mindrot.jbcrypt.BCrypt

class SignIn : LoggedOutPage() {

    private lateinit var buttonSignUp: Button
    private lateinit var buttonReset: Button

    private fun getButtonResetFun(): Button{
        return this.buttonReset
    }

    private fun setButtonResetFun(buttonReset: Button){
        this.buttonReset = buttonReset
    }

    private fun getButtonSignUpFun(): Button{
        return this.buttonSignUp
    }

    private fun setButtonSignUpFun(buttonSignUp: Button){
        this.buttonSignUp = buttonSignUp
    }

    private fun showInvalidCredentialsMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHomePage(userType: String, username: String) {
        val go: Intent = when (userType) {
            "Admin" -> Intent(this, AdminHome::class.java)
            "EventOrg" -> Intent(this, Homepage::class.java)
            else -> Intent(this, Homepage::class.java)
        }

        val json = Json.encodeToString(CurrentUser(username))
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun refresh() { startActivity(Intent(this, SignIn::class.java)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Show the login page
        setContentView(R.layout.activity_login)

        //Get info from text inputs and buttons
        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        setButtonLoginFun(findViewById(R.id.buttonLogin))
        setButtonSignUpFun(findViewById(R.id.buttonSignUp))
        setButtonResetFun(findViewById(R.id.buttonForgot))

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("Users")

        //When user wants to reset password
        getButtonResetFun().setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
        }

        //When user wants to sign up
        getButtonSignUpFun().setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        //When user wants to login
        getButtonLoginFun().setOnClickListener{
            // Validate the username and password
            val username = getEditTextUsernameFun().text.toString()
            val password = getEditTextPasswordFun().text.toString()

            userRef.whereEqualTo("username", username).get().addOnSuccessListener{ documents ->
                val hashed = documents.documents[0].getString("password")
                if((documents.isEmpty || !BCrypt.checkpw(password, hashed))) //Check that account exists
                {
                    showInvalidCredentialsMessage("Invalid username or password.")
                }
                else //if account exists, verify password
                {
                    navigateToHomePage(documents.documents[0].getString("type")!!, username)
                }
            }
        }
    }
}