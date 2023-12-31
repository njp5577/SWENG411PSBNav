package com.example.psbnavigator.controller.appscreens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.Location
import com.example.psbnavigator.model.dataobjects.User
import com.example.psbnavigator.controller.parentpageclasses.LoggedOutPage
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import org.mindrot.jbcrypt.BCrypt

// Controller for the sign up page
class SignUp : LoggedOutPage()
{
    private lateinit var editTextEmail: EditText
    private lateinit var editTextName: EditText
    private lateinit var buttonRegister: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

    //refreshes current page
    override fun refresh() { startActivity(Intent(this, SignUp::class.java)) }

    //on activity create
    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Log.d("SignUpPage", "on create")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeViews()

        //When user wants to go back to sign in page
        getButtonLoginFun().setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }

        //When user wants to sign up
        buttonRegister.setOnClickListener { handleSignUp() }
    }

    //initializes the variables referencing ui components
    private fun initializeViews()
    {
        //Log.d("SignUpPage", "initialize views")

        // Initialize UI components
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextName = findViewById(R.id.editTextName)
        buttonRegister = findViewById(R.id.buttonRegister)
        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        setButtonLoginFun(findViewById(R.id.buttonLogin))
    }

    //gathers input related to sign up
    private fun handleSignUp()
    {
        //Log.d("SignUpPage", "handle signup")

        // Get input values
        val iusername = getEditTextUsernameFun().text.toString()
        val ipassword = getEditTextPasswordFun().text.toString()
        val iemail = editTextEmail.text.toString()
        val iname = editTextName.text.toString()

        // Validate user input
        val validationMessages = validateInput(iusername, iemail, ipassword, iname)

        // Display validation messages if any, else check email existence
        if (validationMessages.isNotEmpty())
        {
            displayValidationMessages(validationMessages)
        }
        else
        {
            checkEmailExistence(iemail, iusername, ipassword, iname)
        }
    }

    //validates user input
    private fun validateInput(iusername: String, iemail: String,
                              ipassword: String, iname: String): List<Pair<Boolean, String>>
    {
        // Validate input fields and provide error messages
        val validationResults = mutableListOf<Pair<Boolean, String>>()

        if (iusername.length < 4)
        {
            validationResults.add(Pair(true, "Username must be at least 4 characters."))
        }

        if (iemail.length < 4)
        {
            validationResults.add(Pair(true, "Email must be at least 4 characters."))
        }

        if (ipassword.length < 4)
        {
            validationResults.add(Pair(true, "Password must be at least 4 characters."))
        }

        if (ipassword.length > 71)
        {
            validationResults.add(Pair(true, "Password must be 72 characters or less."))
        }

        if (iname.length < 2)
        {
            validationResults.add(Pair(true, "Name must be at least 2 characters."))
        }

        Log.d("SignUpPage", "Validation Results: $validationResults")
        return validationResults
    }

    //displays messages related to user input
    private fun displayValidationMessages(validationMessages: List<Pair<Boolean, String>>)
    {
        Log.d("SignUpPage", "display validation messages")
        // Display validation messages as toasts
        validationMessages.forEach { (condition, message) ->
            if (condition) { showToast(message, this) }
        }
    }

    //checks if email is already stored in database
    private fun checkEmailExistence(iemail: String, iusername: String,
                                    ipassword: String, iname: String)
    {
        Log.d("SignUpPage", "check email existence")
        // Check if the email is already associated with an account
        userRef?.whereEqualTo("email", iemail)?.get()?.addOnSuccessListener { documents ->
            if (documents.isEmpty) checkUsernameExistence(iemail, iusername, ipassword, iname)
            else showToast("An account is already under that email.", this)
        }
    }

    //checks if username exists in database
    private fun checkUsernameExistence(iemail: String, iusername: String,
                                       ipassword: String, iname: String)
    {
        userRef?.whereEqualTo("username", iusername)?.get()
            ?.addOnSuccessListener { documents ->
                if(documents.isEmpty) createUserAccount(iemail, iusername, ipassword, iname)
                else showToast("An account is already under that username.", this)
            }
    }

    //create an account for the user and store it in the database
    private fun createUserAccount(iemail: String, iusername: String,
                                  ipassword: String, iname: String)
    {
        Log.d("SignUpPage", "create user account")
        // Hash password
        val hashed = BCrypt.hashpw(ipassword, BCrypt.gensalt())

        userRef?.add(User(iname, iemail, iusername, hashed, "User"))?.addOnSuccessListener {
            val queueRef = DatabaseManager.getDatabaseManager()?.getQueueRef()
            val queue = mutableListOf<Location>()

            queueRef?.add(DestQueue(iusername, queue))?.addOnSuccessListener{
                Log.d("SignUpPage", "Made empty queue")
            }

            //Move to sign in page
            startActivity(Intent(this, SignIn::class.java))
        }
    }
}