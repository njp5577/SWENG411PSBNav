package com.example.loginpageassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ResetPassword : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonReset: Button
    private lateinit var buttonBackLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        var userList = intent.getStringExtra("User List")
        var userL = Json.decodeFromString<ArrayList<User>>(userList.toString())

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonReset = findViewById(R.id.buttonReset)
        buttonBackLogin = findViewById(R.id.buttonBackLogin)
        //If user wants to go back to sign in page
        buttonBackLogin.setOnClickListener {
            val go = Intent(this, SignIn::class.java)
            val json = Json.encodeToString(userL)
            go.putExtra("User List", json)
            startActivity(go)
        }
        //If user wants to reset password
        buttonReset.setOnClickListener {
            // Validate the username and password
            val iusername = editTextUsername.text.toString()
            val ipassword = editTextPassword.text.toString()
            val iemail = editTextEmail.text.toString()
            val iconfirm = editTextConfirmPassword.text.toString()
            var match = 0
            //Check all users to look for a match
            for (user in userL) {
                if (user.username == iusername && user.email == iemail) {
                    match = 1
                    //If a match is found and new password is long enough, reset password
                    if (ipassword == iconfirm && ipassword.length > 3){
                        user.password = ipassword

                        Toast.makeText(this, "Password has been reset.", Toast.LENGTH_SHORT).show()

                        val go = Intent(this, SignIn::class.java)
                        val json = Json.encodeToString(userL)
                        go.putExtra("User List", json)

                        startActivity(go)
                    }
                    else{
                        Toast.makeText(this, "The confirmation password does not match or the password is less than 4 characters.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (match == 0){
                Toast.makeText(this, "No matching user found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}