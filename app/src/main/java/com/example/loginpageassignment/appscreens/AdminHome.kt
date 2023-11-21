package com.example.loginpageassignment.appscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminHome : LoggedInPageAdmin() {

    private lateinit var buttonAddLoc: Button

    private fun getButtonAddLocFun() : Button{
        return this.buttonAddLoc
    }

    private fun setButtonAddLocFun(buttonAddLoc: Button){
        this.buttonAddLoc = buttonAddLoc
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminhome)

        setButtonAddLocFun(findViewById(R.id.buttonAddLoc))
        setButtonBackFun(findViewById(R.id.buttonBack))

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        Toast.makeText(this, "Hello ${getLoggedInAsFun().username}!", Toast.LENGTH_SHORT).show()

        getButtonAddLocFun().setOnClickListener {
            val go = Intent(this, AdminLocAdd::class.java)

            val json = Json.encodeToString(getLoggedInAsFun())

            go.putExtra("User", json)

            startActivity(go)
        }

        getButtonBackFun().setOnClickListener{
            backApp()
        }
    }
}