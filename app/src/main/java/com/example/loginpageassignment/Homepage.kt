package com.example.loginpageassignment
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.json.Json

class Homepage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<User>(userLogin.toString())
        var usern = user.username

        var userList = intent.getStringExtra("User List")
        var userL = Json.decodeFromString<ArrayList<User>>(userList.toString())
        var userLn = userL[0].username

        Toast.makeText(this, "Hello $usern, Hello $userLn (testing access to list)", Toast.LENGTH_SHORT).show()
    }
}