package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.loginpageassignment.R
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.example.loginpageassignment.utilities.managers.DatabaseManager
import com.google.firebase.firestore.QuerySnapshot
import org.mindrot.jbcrypt.BCrypt

//controller for the reset password page
class ResetPassword : LoggedOutPage()
{
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonReset: Button

    //logic for resetting a password
    private fun resetPassword(documents: QuerySnapshot, ipassword: String, iconfirm: String)
    {
        if (ipassword == iconfirm && ipassword.length > 4)
        {
            val hashedPassword = BCrypt.hashpw(ipassword, BCrypt.gensalt())
            documents.documents[0].reference.update("password", hashedPassword)
                .addOnSuccessListener {
                    showToast("Password has been reset.", this)
                    // Go to login screen
                    startActivity(Intent(this, SignIn::class.java))
            }
        }
    }

    //refresh the current page
    override fun refresh() { startActivity(Intent(this, ResetPassword::class.java)) }

    //initialize variables with user input
    private fun initializeViews()
    {
        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonReset = findViewById(R.id.buttonReset)
        setButtonLoginFun(findViewById(R.id.buttonLogin))
    }

    //on activity create
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

        //initialize vars
        initializeViews()

        //If user wants to go back to sign in page
        getButtonLoginFun().setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }

        //If user wants to reset password
        buttonReset.setOnClickListener {
            // get input from variables
            val iusername = getEditTextUsernameFun().text.toString()
            val ipassword = getEditTextPasswordFun().text.toString()
            val iemail = editTextEmail.text.toString()
            val iconfirm = editTextConfirmPassword.text.toString()

            //Check all users to look for a match
            userRef?.whereEqualTo("username", iusername)?.whereEqualTo("email", iemail)
                ?.get()?.addOnSuccessListener{ documents ->
                    //Check if incorrect credentials
                    if (documents.isEmpty) {
                        showToast("Invalid username or email.", this)
                    } else {
                        resetPassword(documents, ipassword, iconfirm)
                    }
                }
        }
    }
}