package com.example.loginpageassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//Object for user information
@Serializable
data class User (
    var name: String,
    var email: String,
    var username: String,
    var password: String
)

@Serializable
data class CurrentUser (
    var username: String
)

class SignIn : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignUp: Button
    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Show the login page
        setContentView(R.layout.activity_login)
        //Get info from text inputs and buttons
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonReset = findViewById(R.id.buttonForgot)

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var userRef = firestore.collection("Users")

        //When user wants to reset password
        buttonReset.setOnClickListener {
            val go = Intent(this, ResetPassword::class.java)
            startActivity(go)
        }

        //When user wants to sign up
        buttonSignUp.setOnClickListener {
            val go = Intent(this, SignUp::class.java)
            startActivity(go)
        }

        //When user wants to login
        buttonLogin.setOnClickListener {
            // Validate the username and password
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            var notFound = 1

            userRef.whereEqualTo("username", username).whereEqualTo("password", password).get().addOnSuccessListener{ documents ->
                //Check that account does not already exist
                if ((documents.isEmpty)) {
                    Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show()
                    notFound = 0
                }

                //Create account if there are no issues
                if(notFound == 1) {

                    // Go to login screen
                    val go = Intent(this, Homepage::class.java)

                    val json = Json.encodeToString(CurrentUser(username))

                    go.putExtra("User", json)

                    startActivity(go)

                }
            }
        }
    }
}