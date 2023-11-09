package com.example.loginpageassignment

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.json.Json


class Homepage : AppCompatActivity() {

    private lateinit var googleMapWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initializeMap()

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString()).username


        Toast.makeText(this, "Hello $user", Toast.LENGTH_SHORT).show()
    }

    private fun initializeMap() {
        val iframe =
            "<iframe src=https://www.google.com/maps/embed?pb=!1m24!1m12!1m3!1d14050.37982627822!2d-79.98861572345025!3d42.11829984365602!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!4m9!3e2!4m3!3m2!1d42.119999299999996!2d-79.98114729999999!4m3!3m2!1d42.1167723!2d-79.97656099999999!5e1!3m2!1sen!2sus!4v1699136150186!5m2!1sen!2sus width=100% height=100% frameborder=0 style=border:0</iframe>"
        googleMapWebView = findViewById<View>(R.id.googlemap_webView) as WebView
        googleMapWebView.getSettings().setJavaScriptEnabled(true)
        googleMapWebView.loadData(iframe, "text/html", "utf-8")
    }
}