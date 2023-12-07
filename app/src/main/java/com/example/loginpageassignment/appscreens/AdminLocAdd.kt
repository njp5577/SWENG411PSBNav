package com.example.loginpageassignment.appscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminLocAdd : LoggedInPageAdmin() {

    private lateinit var editTextName: EditText
    private lateinit var editTextLatitude: EditText
    private lateinit var editTextLongitude: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var buttonAddLoc: Button

    private fun getEditTextNameFun() : EditText{
        return this.editTextName
    }

    private fun setEditTextNameFun(editTextName: EditText){
        this.editTextName = editTextName
    }

    private fun getEditTextLatitudeFun() : EditText{
        return this.editTextLatitude
    }

    private fun setEditTextLatitudeFun(editTextLatitude: EditText){
        this.editTextLatitude = editTextLatitude
    }

    private fun getEditTextLongitudeFun() : EditText{
        return this.editTextLongitude
    }

    private fun setEditTextLongitudeFun(editTextLongitude: EditText){
        this.editTextLongitude = editTextLongitude
    }

    private fun getEditTextDescFun() : EditText{
        return this.editTextDesc
    }

    private fun setEditTextDescFun(editTextDesc: EditText){
        this.editTextDesc = editTextDesc
    }

    private fun getButtonAddLocFun() : Button{
        return this.buttonAddLoc
    }

    private fun setButtonAddLocFun(buttonAddLoc: Button){
        this.buttonAddLoc = buttonAddLoc
    }

    override fun refresh()
    {
        val go = Intent(this, AdminLocAdd::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminlocadd)

        setEditTextNameFun(findViewById(R.id.editTextName))
        setEditTextLatitudeFun(findViewById(R.id.editTextLatitude))
        setEditTextLongitudeFun(findViewById(R.id.editTextLongitude))
        setEditTextDescFun(findViewById(R.id.editTextDesc))
        setButtonAddLocFun(findViewById(R.id.buttonAddLoc))

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var locRef = firestore.collection("Locations")

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        buttonAddLoc.setOnClickListener {

            val iname = getEditTextNameFun().text.toString()
            val ilat = getEditTextLatitudeFun().text.toString()
            val ilong = getEditTextLongitudeFun().text.toString()
            val idesc = getEditTextDescFun().text.toString()

            //Check that user is inputting appropriate number of characters for each field
            if (iname.length < 4){
                Toast.makeText(this, "Name must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (idesc.length < 4){
                Toast.makeText(this, "Description must be at least 4 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var lat = 0.0

            var long = 0.0

            try{
                lat = ilat.toDouble()
                long = ilong.toDouble()
            }
            catch(e: NumberFormatException){
                Toast.makeText(this, "Latitude and longitude must be decimal numbers.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            locRef.whereEqualTo("name", iname).get().addOnSuccessListener{ documents ->
                //Check that location does not already exist
                if (!(documents.isEmpty)) {
                    Toast.makeText(this, "A location with this name already exists.", Toast.LENGTH_SHORT).show()

                }
                else{
                    //Create location if there are no issues

                    locRef.add(Location(iname, lat, long, idesc)).addOnSuccessListener {
                        val message = "Location added at " + lat + ", " + long

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }
}