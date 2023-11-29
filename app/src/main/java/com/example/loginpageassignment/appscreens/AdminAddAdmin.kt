package com.example.loginpageassignment.appscreens
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.json.Json

class AdminAddAdmin : LoggedInPageAdmin() {

    private lateinit var editTextEmail: EditText
    private lateinit var buttonAdmin: Button

    // Reference to the "Users" collection in Firestore
    private val userRef = FirebaseFirestore.getInstance().collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminaddadmin)

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonAdmin.setOnClickListener { handleEventOrg() }
    }

    private fun initializeView(){
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonAdmin = findViewById(R.id.buttonAdmin)
    }

    private fun handleEventOrg(){
        val iemail = editTextEmail.text.toString()

        //Check all users to look for a match
        userRef.whereEqualTo("email", iemail).get().addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty)
            {
                Toast.makeText(this, "No account under that email.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                changeToEventOrg(documents)
            }
        }
    }

    private fun changeToEventOrg(documents : QuerySnapshot){
        documents.documents[0].reference.update("type", "Admin").addOnSuccessListener {
            Toast.makeText(this, "The user under this email has been turned into an admin.", Toast.LENGTH_SHORT).show()
        }
    }
}