package com.example.loginpageassignment.appscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminLocDelete: LoggedInPageAdmin() {

    private lateinit var editTextName: EditText
    private lateinit var buttonDelete: Button

    // Reference to the "Users" collection in Firestore
    private val locRef = FirebaseFirestore.getInstance().collection("Locations")

    override fun refresh()
    {
        val go = Intent(this, AdminLocDelete::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminlocdelete)

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonDelete.setOnClickListener { handleDeleteUser() }
    }

    private fun initializeView(){
        editTextName = findViewById(R.id.editTextName)
        buttonDelete = findViewById(R.id.buttonDelete)
    }

    private fun handleDeleteUser(){
        val iname = editTextName.text.toString()

        //Check all users to look for a match
        locRef.whereEqualTo("name", iname).get().addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty)
            {
                Toast.makeText(this, "No location under this name.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                deleteLoc(documents)
            }
        }
    }

    private fun deleteLoc(documents : QuerySnapshot){
        documents.documents[0].reference.delete().addOnSuccessListener {
            Toast.makeText(this, "The location under this name has been deleted.", Toast.LENGTH_SHORT).show()
        }
    }
}