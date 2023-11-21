package com.example.loginpageassignment.appscreens
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DestQueue(
    var list: MutableList<Location>
)

class DestinationQueue : LoggedInPageAdmin() {

    private lateinit var buttonAdd: Button
    private lateinit var buttonRetrieve: Button

    private fun getButtonAddFun() : Button {
        return this.buttonAdd
    }

    private fun setButtonAddFun(buttonAdd: Button){
        this.buttonAdd = buttonAdd
    }

    private fun getButtonRetrieveFun() : Button {
        return this.buttonRetrieve
    }

    private fun setButtonRetrieveFun(buttonRetrieve: Button){
        this.buttonRetrieve = buttonRetrieve
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinationqueue)

        setButtonAddFun(findViewById(R.id.buttonAdd))
        setButtonRetrieveFun(findViewById(R.id.buttonRetrieve))

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        var firestore: FirebaseFirestore;

        firestore = FirebaseFirestore.getInstance()

        var queueRef = firestore.collection("Queues")

        getButtonAddFun().setOnClickListener {
            var list = mutableListOf(Location("Name1",1.5, 1.1,"Desc1"), Location("Name2",12.5, 13.5, "Desc2"))

            queueRef.add(DestQueue(list)).addOnSuccessListener {
                Toast.makeText(this, "Added ${DestQueue(list)}!", Toast.LENGTH_SHORT).show()
            }
        }

        getButtonRetrieveFun().setOnClickListener {
            queueRef.get().addOnSuccessListener{ documents ->
                if ((documents.isEmpty)) {
                    Toast.makeText(this, "No list in DB", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Retrieved ${documents.documents[0].get("list")}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}