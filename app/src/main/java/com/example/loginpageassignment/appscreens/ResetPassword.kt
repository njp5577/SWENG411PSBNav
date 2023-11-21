package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.google.firebase.firestore.FirebaseFirestore

class ResetPassword : LoggedOutPage() {

    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonReset: Button

    private fun getEditTextConfirmPasswordFun() : EditText{
        return this.editTextConfirmPassword
    }

    private fun setEditTextConfirmPasswordFun(editTextConfirmPassword: EditText){
        this.editTextConfirmPassword = editTextConfirmPassword
    }

    private fun getEditTextEmailFun() : EditText{
        return this.editTextEmail
    }

    private fun setEditTextEmailFun(editTextEmail: EditText){
        this.editTextEmail = editTextEmail
    }

    private fun getButtonResetFun(): Button{
        return this.buttonReset
    }

    private fun setButtonResetFun(buttonReset: Button){
        this.buttonReset = buttonReset
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var userRef = firestore.collection("Users")

        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        setEditTextConfirmPasswordFun(findViewById(R.id.editTextConfirmPassword))
        setEditTextEmailFun(findViewById(R.id.editTextEmail))
        setButtonResetFun(findViewById(R.id.buttonReset))
        setButtonLoginFun(findViewById(R.id.buttonLogin))
        //If user wants to go back to sign in page
        getButtonLoginFun().setOnClickListener {
            val go = Intent(this, SignIn::class.java)
            startActivity(go)
        }
        //If user wants to reset password
        getButtonResetFun().setOnClickListener {
            // Validate the username and password
            val iusername = getEditTextUsernameFun().text.toString()
            val ipassword = getEditTextPasswordFun().text.toString()
            val iemail = getEditTextEmailFun().text.toString()
            val iconfirm = getEditTextConfirmPasswordFun().text.toString()
            var match = 0

            //Check all users to look for a match
            userRef.whereEqualTo("username", iusername).whereEqualTo("email", iemail).get().addOnSuccessListener{ documents ->
                //Check if incorrect credentials
                if ((documents.isEmpty)) {
                    Toast.makeText(this, "Invalid username or email.", Toast.LENGTH_SHORT).show()
                    match = 0
                }
                else{
                    match = 1
                }

                //Reset password if there are no issues
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