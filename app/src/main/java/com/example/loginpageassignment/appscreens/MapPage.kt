package com.example.loginpageassignment.appscreens
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.parentpageclasses.LoggedInPageAdmin
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Timer
import kotlin.concurrent.schedule

class MapPage : LoggedInPageAdmin() {

    private lateinit var googleMapWebView: WebView
    private lateinit var buttonNext: Button
    private lateinit var buttonPrev: Button
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    // Reference to the "Queues" collection in Firestore
    private val queueRef = FirebaseFirestore.getInstance().collection("Queues")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun refresh()
    {
        val go = Intent(this, MapPage::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mappage)

        var userLogin = intent.getStringExtra("User")
        var user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)


        initializeView()


        //When user wants to sign up
        buttonNext.setOnClickListener {
            handleNextDestination()
        }

        buttonPrev.setOnClickListener {
            handlePrevDestination()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeView(){
        buttonNext = findViewById(R.id.buttonNext)
        buttonPrev = findViewById(R.id.buttonPrev)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                            Toast.makeText(this, "Location access granted.", Toast.LENGTH_SHORT).show()

                            if(isLocationEnabled()) {
                                val result = fusedLocationClient.getCurrentLocation(
                                    Priority.PRIORITY_HIGH_ACCURACY,
                                    CancellationTokenSource().token
                                )
                                result.addOnCompleteListener {
                                    currentLatitude = it.result.latitude
                                    currentLongitude = it.result.longitude

                                    Log.d("stuff", currentLatitude.toString() + ", " + currentLongitude.toString())

                                    Timer().schedule(5000){
                                        initializeMap()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(this, "Turn on the location to continue.", Toast.LENGTH_SHORT).show()

                                createLocationRequest()
                            }
                        }

                        else -> {
                            Toast.makeText(this, "No location access granted.", Toast.LENGTH_SHORT).show()
                        }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this, 100
                    )
                } catch (sendEx: java.lang.Exception){

                }
            }
        }
    }

    //42.119320, -79.987709
    //https://www.google.com/maps/embed?pb=!1m24!1m12!1m3!1d14050.37982627822!2d-79.98861572345025!3d42.11829984365602!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!4m9!3e2!4m3!3m2!1d42.119999299999996!2d-79.98114729999999!4m3!3m2!1d42.1167723!2d-79.97656099999999!5e1!3m2!1sen!2sus!4v1699136150186!5m2!1sen!2sus
    private fun initializeMap() {

        queueRef.whereEqualTo("user", getLoggedInAsFun().username).get().addOnSuccessListener{ documents ->
            val destQueueList = mutableListOf<DestQueue>()
            for (document in documents) {
                val destQueue = document.toObject(DestQueue::class.java)
                destQueueList.add(destQueue)
            }

            val destLat = destQueueList[0].list[0].latitude
            val destLong = destQueueList[0].list[0].longitude

            Log.d("stuffcoord1", destLat.toString() + ", " + destLong.toString())
            Log.d("stuffmine", currentLatitude.toString() + ", " + currentLongitude.toString())

            val iframe =
                "<iframe src=https://www.google.com/maps/embed?pb=!1m24!1m12!1m3!1d14050.37982627822!2d-79.987709!3d42.119320!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!4m9!3e2!4m3!3m2!1d$currentLatitude!2d$currentLongitude!4m3!3m2!1d$destLat!2d$destLong!5e1!3m2!1sen!2sus!4v1699136150186!5m2!1sen!2sus width=100% height=100% frameborder=0 style=border:0</iframe>"
            googleMapWebView = findViewById<View>(R.id.googlemap_webView) as WebView
            googleMapWebView.getSettings().setJavaScriptEnabled(true)
            googleMapWebView.loadData(iframe, "text/html", "utf-8")
        }
    }

    private fun handleNextDestination(){
        queueRef.whereEqualTo("user", getLoggedInAsFun().username).get().addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty)
            {
                Toast.makeText(this, "This user does not have a destination queue.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val destQueueList = mutableListOf<DestQueue>()
                for (document in documents) {
                    val destQueue = document.toObject(DestQueue::class.java)
                    destQueueList.add(destQueue)
                }

                if(destQueueList[0].list.isEmpty()){
                    Toast.makeText(this, "Need two or more destinations in queue to do shuffling. You have zero.", Toast.LENGTH_SHORT).show()
                }
                else if (destQueueList[0].list.size == 1){
                    Toast.makeText(this, "Need two or more destinations in queue to do shuffling. You have one.", Toast.LENGTH_SHORT).show()
                }
                else{
                    val newQueue = shuffleQueueForward(destQueueList[0].list)

                    documents.documents[0].reference.update("list", newQueue).addOnSuccessListener {
                        Toast.makeText(this, "Destination queue shuffled forward one place.", Toast.LENGTH_SHORT).show()

                        initializeMap()
                    }
                }
            }
        }
    }

    private fun handlePrevDestination(){
        queueRef.whereEqualTo("user", getLoggedInAsFun().username).get().addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty)
            {
                Toast.makeText(this, "This user does not have a destination queue.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val destQueueList = mutableListOf<DestQueue>()
                for (document in documents) {
                    val destQueue = document.toObject(DestQueue::class.java)
                    destQueueList.add(destQueue)
                }

                if(destQueueList[0].list.isEmpty()){
                    Toast.makeText(this, "Need two or more destinations in queue to do shuffling. You have zero.", Toast.LENGTH_SHORT).show()
                }
                else if (destQueueList[0].list.size == 1){
                    Toast.makeText(this, "Need two or more destinations in queue to do shuffling. You have one.", Toast.LENGTH_SHORT).show()
                }
                else{
                    val newQueue = shuffleQueueBackward(destQueueList[0].list)

                    documents.documents[0].reference.update("list", newQueue).addOnSuccessListener {
                        Toast.makeText(this, "Destination queue shuffled backward one place.", Toast.LENGTH_SHORT).show()

                        initializeMap()
                    }
                }
            }
        }
    }

    private fun shuffleQueueForward(destqueue: MutableList<Location>): MutableList<Location>{
        val newDestQueue = mutableListOf<Location>()
        val temp = destqueue.elementAt(0)

        for(i in 1..(destqueue.size - 1)){
            newDestQueue.add(destqueue.elementAt(i))
        }

        newDestQueue.add(temp)

        return newDestQueue
    }

    private fun shuffleQueueBackward(destqueue: MutableList<Location>): MutableList<Location>{
        val newDestQueue = mutableListOf<Location>()
        val temp = destqueue.elementAt(destqueue.size - 1)

        newDestQueue.add(temp)

        for(i in 1..(destqueue.size - 1)){
            newDestQueue.add(destqueue.elementAt(i - 1))
        }

        return newDestQueue
    }
}