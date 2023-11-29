package com.example.loginpageassignment.appscreens

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.PSB_Event
import com.example.loginpageassignment.parentpageclasses.LoggedInPageEventOrg
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json

class EventOrgManageEvents : LoggedInPageEventOrg() {

    private lateinit var editTextName: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonEvent: Button

    private val eventRef = FirebaseFirestore.getInstance().collection("Events")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventorgmanageevents)

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonEvent.setOnClickListener { handleEventAdd() }
    }

    private fun initializeView(){
        editTextName = findViewById(R.id.editTextName)
        editTextLocation = findViewById(R.id.editTextLocation)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonEvent = findViewById(R.id.buttonEvent)
    }

    private fun handleEventAdd(){
        val iname = editTextName.text.toString()
        val ilocation = editTextLocation.text.toString()
        val idate = editTextDate.text.toString()
        val itime = editTextTime.text.toString()
        val idescription = editTextDescription.text.toString()

        //Check that user is inputting appropriate number of characters for each field
        if (iname.length < 4){
            Toast.makeText(this, "Name must be at least 4 characters.", Toast.LENGTH_SHORT).show()
        }
        else if (idescription.length < 4){
            Toast.makeText(this, "Description must be at least 4 characters.", Toast.LENGTH_SHORT).show()
        }
        else {
            eventRef.add(PSB_Event(iname, ilocation, idate, itime, idescription)).addOnSuccessListener {
                val message = "Event added with name: " + iname

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                editTextName.setText("")
                editTextLocation.setText("")
                editTextDate.setText("")
                editTextTime.setText("")
                editTextDescription.setText("")
            }
        }
    }


}