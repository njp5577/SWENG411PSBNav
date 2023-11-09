package com.example.loginpageassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

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

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var userRef = firestore.collection("Users")

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextName = findViewById(R.id.editTextName)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonBackLogin = findViewById(R.id.buttonBackLogin)
        //When user wants to go back to sign in page
        buttonBackLogin.setOnClickListener {
            val go = Intent(this, SignIn::class.java)
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

            if(inUse == 0){
                userRef.whereEqualTo("email", iemail).get().addOnSuccessListener{ documents ->
                    //Check that account does not already exist
                    if (!(documents.isEmpty)) {
                        Toast.makeText(this, "An account is already under that email.", Toast.LENGTH_SHORT).show()
                        inUse = 1
                    }

                    //Create account if there are no issues
                    if(inUse == 0) {
                        userRef.add(User(iname, iemail, iusername, ipassword)).addOnSuccessListener {
                            // Go to login screen
                            val go = Intent(this, SignIn::class.java)

                            startActivity(go)
                        }
                    }
                }
            }
        }
    }
}