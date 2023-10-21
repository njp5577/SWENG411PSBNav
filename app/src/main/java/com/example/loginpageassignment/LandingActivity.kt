package com.example.loginpageassignment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.json.Json

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        var userList = intent.getStringExtra("User List")
        var user = Json.decodeFromString<User>(userList.toString())
    }
}