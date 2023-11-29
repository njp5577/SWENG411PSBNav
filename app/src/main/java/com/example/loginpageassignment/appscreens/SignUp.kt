package com.example.loginpageassignment.appscreens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.dataobjects.User
import com.example.loginpageassignment.parentpageclasses.LoggedOutPage
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

class SignUp : LoggedOutPage()
{
    private lateinit var editTextEmail: EditText
    private lateinit var editTextName: EditText
    private lateinit var buttonRegister: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = FirebaseFirestore.getInstance().collection("Users")

    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.d("SignUpPage", "on create")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeViews()

        //When user wants to go back to sign in page
        getButtonLoginFun().setOnClickListener { startActivity(Intent(this, SignIn::class.java)) }

        //When user wants to sign up
        buttonRegister.setOnClickListener { handleSignUp() }
    }

    private fun initializeViews()
    {
        Log.d("SignUpPage", "initialize views")

        // Initialize UI components
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextName = findViewById(R.id.editTextName)
        buttonRegister = findViewById(R.id.buttonRegister)
        setEditTextUsernameFun(findViewById(R.id.editTextUsername))
        setEditTextPasswordFun(findViewById(R.id.editTextPassword))
        setButtonLoginFun(findViewById(R.id.buttonLogin))
    }

    private fun handleSignUp()
    {
        Log.d("SignUpPage", "handle signup")

        // Get input values
        val iusername = getEditTextUsernameFun().text.toString()
        val ipassword = getEditTextPasswordFun().text.toString()
        val iemail = editTextEmail.text.toString()
        val iname = editTextName.text.toString()
        val type = "User"

        // Validate user input
        val validationMessages = validateInput(iusername, iemail, ipassword, iname)

        // Display validation messages if any, else check email existence
        if (validationMessages.isNotEmpty())
        {
            displayValidationMessages(validationMessages)
        }
        else
        {
            checkEmailExistence(iemail, iusername, ipassword, iname, type)
        }
    }

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

    private fun displayValidationMessages(validationMessages: List<Pair<Boolean, String>>)
    {
        Log.d("SignUpPage", "display validation messages")
        // Display validation messages as toasts
        validationMessages.forEach { (condition, message) ->
            if (condition) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmailExistence(iemail: String, iusername: String,
                                    ipassword: String, iname: String, type: String)
    {
        Log.d("SignUpPage", "check email existence")
        // Check if the email is already associated with an account
        userRef.whereEqualTo("email", iemail).get().addOnSuccessListener { documents ->
            if (documents.isEmpty)
                userRef.whereEqualTo("username", iname).get().addOnSuccessListener { documentsTwo ->
                    if(documentsTwo.isEmpty){
                        createUserAccount(iemail, iusername, ipassword, iname, type)
                    }
                    else{
                        Toast.makeText(this, "An account is already under that username.", Toast.LENGTH_SHORT).show()
                    }
                }
            else
                Toast.makeText(this, "An account is already under that email.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUserAccount(iemail: String, iusername: String,
                                  ipassword: String, iname: String, type: String)
    {
        Log.d("SignUpPage", "create user account")
        // Hash password
        val hashed = BCrypt.hashpw(ipassword, BCrypt.gensalt())

        userRef.add(User(iname, iemail, iusername, hashed, type)).addOnSuccessListener {
            val queueRef = FirebaseFirestore.getInstance().collection("Queues")
            val queue = mutableListOf<Location>()

            queueRef.add(DestQueue(iusername, queue)).addOnSuccessListener{
                Log.d("SignUpPage", "Made empty queue")
            }

            //Move to sign in page
            startActivity(Intent(this, SignIn::class.java))
        }
    }
}