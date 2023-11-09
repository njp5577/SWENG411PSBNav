package com.example.loginpageassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

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

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var userRef = firestore.collection("Users")

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonReset = findViewById(R.id.buttonReset)
        buttonBackLogin = findViewById(R.id.buttonBackLogin)
        //If user wants to go back to sign in page
        buttonBackLogin.setOnClickListener {
            val go = Intent(this, SignIn::class.java)
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
            // Validate the username and password

            userRef.whereEqualTo("username", iusername).whereEqualTo("email", iemail).get().addOnSuccessListener{ documents ->
                //Check that account does not already exist
                if ((documents.isEmpty)) {
                    Toast.makeText(this, "Invalid username or email.", Toast.LENGTH_SHORT).show()
                    match = 0
                }
                else{
                    match = 1
                }

                //Create account if there are no issues
                if(match == 1) {
                    if (ipassword == iconfirm && ipassword.length > 3){
                        documents.documents[0].reference.update("password", ipassword).addOnSuccessListener {
                            Toast.makeText(this, "Password has been reset.", Toast.LENGTH_SHORT).show()
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