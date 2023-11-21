package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.User
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : LoggedOutPage() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextName: EditText
    private lateinit var buttonRegister: Button

    private fun getEditTextEmailFun() : EditText{
        return this.editTextEmail
    }

    private fun setEditTextEmailFun(editTextEmail: EditText){
        this.editTextEmail = editTextEmail
    }

    private fun setEditTextNameFun(editTextName: EditText){
        this.editTextName = editTextName
    }

    private fun getEditTextNameFun() : EditText{
        return this.editTextName
    }

    private fun getButtonRegisterFun(): Button{
        return this.buttonRegister
    }

    private fun setButtonRegisterFun(buttonRegister: Button){
        this.buttonRegister = buttonRegister
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var userRef = firestore.collection("Users")

        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        setEditTextEmailFun(findViewById(R.id.editTextEmail))
        setEditTextNameFun(findViewById(R.id.editTextName))
        setButtonRegisterFun(findViewById(R.id.buttonRegister))
        setButtonLoginFun(findViewById(R.id.buttonLogin))
        //When user wants to go back to sign in page
        getButtonLoginFun().setOnClickListener {
            val go = Intent(this, SignIn::class.java)
            startActivity(go)
        }
        //When user wants to sign up
        getButtonRegisterFun().setOnClickListener {
            // Validate the username and password
            val iusername = getEditTextUsernameFun().text.toString()
            val ipassword = getEditTextPasswordFun().text.toString()
            val iemail = getEditTextEmailFun().text.toString()
            val iname = getEditTextNameFun().text.toString()
            val type = "User"
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
                        userRef.add(User(iname, iemail, iusername, ipassword, type)).addOnSuccessListener {
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