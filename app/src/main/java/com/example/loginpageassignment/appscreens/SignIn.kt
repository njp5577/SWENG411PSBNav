package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var userRef = firestore.collection("Users")

        //When user wants to reset password
        getButtonResetFun().setOnClickListener {
            val go = Intent(this, ResetPassword::class.java)
            startActivity(go)
        }

        //When user wants to sign up
        getButtonSignUpFun().setOnClickListener {
            val go = Intent(this, SignUp::class.java)
            startActivity(go)
        }

        //When user wants to login
        getButtonLoginFun().setOnClickListener {
            // Validate the username and password
            val username = getEditTextUsernameFun().text.toString()
            val password = getEditTextPasswordFun().text.toString()
            var notFound = 1

            userRef.whereEqualTo("username", username).whereEqualTo("password", password).get().addOnSuccessListener{ documents ->
                //Check that account exists
                if ((documents.isEmpty)) {
                    Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show()
                    notFound = 0
                }


                if(notFound == 1) {

                    if (documents.documents[0].get("type") == "Admin"){
                        // Go to admin homepage
                        val go = Intent(this, AdminHome::class.java)

                        val json = Json.encodeToString(CurrentUser(username))

                        go.putExtra("User", json)

                        startActivity(go)
                    }
                    else if (documents.documents[0].get("type") == "EventOrg"){
                        // Go to event organizer homepage
                        val go = Intent(this, Homepage::class.java)

                        val json = Json.encodeToString(CurrentUser(username))

                        go.putExtra("User", json)

                        startActivity(go)
                    }
                    else{
                        // Go to user homepage
                        val go = Intent(this, Homepage::class.java)

                        val json = Json.encodeToString(CurrentUser(username))

                        go.putExtra("User", json)

                        startActivity(go)
                    }

                }
            }
        }
    }
}