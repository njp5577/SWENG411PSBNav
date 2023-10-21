package com.example.loginpageassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
//List containing users
@Serializable
var userList = arrayListOf(
    User(
        name = "admin",
        email = "email@email.com",
        username = "admin",
        password = "admin"
    )
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
        //Get userList from other activity if it is available
        var newUserList = intent.getStringExtra("User List")
        if (newUserList != null){
            userList = Json.decodeFromString(newUserList.toString())
        }
        //Get info from text inputs and buttons
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonReset = findViewById(R.id.buttonForgot)

        //When user wants to reset password
        buttonReset.setOnClickListener {
            val go = Intent(this, ResetPassword::class.java)
            val json = Json.encodeToString(userList)
            go.putExtra("User List", json)
            startActivity(go)
        }

        //When user wants to sign up
        buttonSignUp.setOnClickListener {
            val go = Intent(this, SignUp::class.java)
            val json = Json.encodeToString(userList)
            go.putExtra("User List", json)
            startActivity(go)
        }

        //When user wants to login
        buttonLogin.setOnClickListener {
            // Validate the username and password
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            var notFound = 1

            for (user in userList) {
                if (user.username == username && user.password == password) {
                    // Login successful
                    val go = Intent(this, Homepage::class.java)
                    val json = Json.encodeToString(user)
                    val jsonTwo = Json.encodeToString(userList)
                    go.putExtra("User", json)
                    go.putExtra("User List", jsonTwo)
                    startActivity(go)
                    notFound = 0
                    break
                }
                else {
                    notFound = 1
                }
            }
            //If login fails, tell user
            if (notFound == 1){
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}