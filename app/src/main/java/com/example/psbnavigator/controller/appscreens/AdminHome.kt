package com.example.psbnavigator.controller.appscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageAdmin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminHome : LoggedInPageAdmin()
{
    private lateinit var buttonAddLoc: Button
    private lateinit var buttonDeleteLoc: Button
    private lateinit var buttonAddAdmin: Button
    private lateinit var buttonAddEventOrg: Button
    private lateinit var buttonDeleteAccount: Button
    private lateinit var buttonLogout: Button

    private fun getButtonAddLocFun() : Button{ return this.buttonAddLoc }

    private fun setButtonAddLocFun(buttonAddLoc: Button){ this.buttonAddLoc = buttonAddLoc }

    override fun refresh()
    {
        val go = Intent(this, AdminHome::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminhome)

        setButtonAddLocFun(findViewById(R.id.buttonAddLoc))
        buttonDeleteLoc = findViewById(R.id.buttonDeleteLoc)
        buttonAddAdmin = findViewById(R.id.buttonAddAdmin)
        buttonAddEventOrg = findViewById(R.id.buttonAddEventOrg)
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount)
        buttonLogout = findViewById(R.id.buttonLogout)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        showToast("Hello ${getLoggedInAsFun().username}!", this)

        getButtonAddLocFun().setOnClickListener {
            val go = Intent(this, AdminLocAdd::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonDeleteLoc.setOnClickListener {
            val go = Intent(this, AdminLocDelete::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonAddAdmin.setOnClickListener {
            val go = Intent(this, AdminAddAdmin::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonAddEventOrg.setOnClickListener {
            val go = Intent(this, AdminAddEventOrg::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonDeleteAccount.setOnClickListener{
            val go = Intent(this, AdminUserDelete::class.java)
            val json = Json.encodeToString(getLoggedInAsFun())
            go.putExtra("User", json)
            startActivity(go)
        }

        buttonLogout.setOnClickListener{
            val go = Intent(this, SignIn::class.java)
            startActivity(go)

        }
    }
}