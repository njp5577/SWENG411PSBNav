package com.example.loginpageassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SignUp : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextName: EditText
    private lateinit var buttonRegister: Button
    private lateinit var buttonBackLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var userList = intent.getStringExtra("User List")
        var userL = Json.decodeFromString<ArrayList<User>>(userList.toString())

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextName = findViewById(R.id.editTextName)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonBackLogin = findViewById(R.id.buttonBackLogin)
        //When user wants to go back to sign in page
        buttonBackLogin.setOnClickListener {
            val go = Intent(this, SignIn::class.java)
            val json = Json.encodeToString(userL)
            go.putExtra("User List", json)
            startActivity(go)
        }
        //When user wants to sign up
        buttonRegister.setOnClickListener {
            // Validate the username and password
            val iusername = editTextUsername.text.toString()
            val ipassword = editTextPassword.text.toString()
            val iemail = editTextEmail.text.toString()
            val iname = editTextName.text.toString()
            var inUse = 0
            //Check that account does not already exist
            for (user in userL) {
                if (user.username == iusername || user.email == iemail) {
                    Toast.makeText(this, "An account is already under that name or email.", Toast.LENGTH_SHORT).show()
                    inUse = 1
                }
            }
            //Check that user is inputting appropriate number of characters for each field
            if (iusername.length < 4){
                Toast.makeText(this, "Username must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                inUse = 1
            }
            else if (iemail.length < 4){
                Toast.makeText(this, "Email must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                inUse = 1
            }
            else if (ipassword.length < 4){
                Toast.makeText(this, "Password must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                inUse = 1
            }
            else if (iname.length < 2){
                Toast.makeText(this, "Name must be at least 1 character.", Toast.LENGTH_SHORT).show()
                inUse = 1
            }
            //Create account if there are no issues
            if(inUse == 0) {
                userL.add(
                    User(
                        email = iemail,
                        username = iusername,
                        password = ipassword,
                        name = iname
                    )
                )

                // Registration
                val go = Intent(this, SignIn::class.java)
                val json = Json.encodeToString(userL)
                go.putExtra("User List", json)

                startActivity(go)
            }
        }
    }
}