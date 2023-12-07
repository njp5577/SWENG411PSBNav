//DestinationQueue.kt
package com.example.loginpageassignment.appscreens
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.dataobjects.PSB_Event
import com.example.loginpageassignment.parentpageclasses.LoggedInPage
import com.example.loginpageassignment.utilities.managers.DatabaseManager
import com.example.loginpageassignment.utilities.popup.AddToQueuePopup
import com.example.loginpageassignment.utilities.managers.QueueManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DestQueue(var user: String, var list: MutableList<Location>)
{
    constructor() : this("", mutableListOf())
}

/*
TODO:
    possibly extract adapter to its own class and set up generalization relationship with event adapter
*/
class DestinationQueue : LoggedInPage(), AddToQueuePopup.PopupDismissCallback
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var destQueueAdapter: DestQueueAdapter
    private lateinit var addToQueueButton: Button
    private var addToQueuePopup = AddToQueuePopup(this, this)

    override fun refresh()
    {
        val go = Intent(this, DestinationQueue::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinationqueue)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        recyclerView = findViewById(R.id.destQueueRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        destQueueAdapter = DestQueueAdapter(getLoggedInAsFun().username, this)
        recyclerView.adapter = destQueueAdapter

        val queueRef = DatabaseManager.getDatabaseManager()?.getQueueRef()

        addToQueueButton = findViewById(R.id.addToQueueButton)
        addToQueueButton.setOnClickListener {
            //open popup
            addToQueuePopup.showDetails(PSB_Event(), getLoggedInAsFun())
        }

        queueRef?.get()?.addOnSuccessListener { documents ->
            if (documents.isEmpty)
            {
                // Handle the case where there's no data in the database
                Log.e("DestQueuePage", "No documents")
            }
            else
            {
                val destQueueList = mutableListOf<DestQueue>()

                // Find user's queue
                for (document in documents)
                {
                    if (document.getString("user") == getLoggedInAsFun().username)
                    {
                        val list = mutableListOf<Location>()
                        val rawList = document["list"] as ArrayList<HashMap<String, Any>>?

                        rawList?.forEach { locationMap ->
                            val name = locationMap["name"] as String
                            val latitude = locationMap["latitude"] as Double
                            val longitude = locationMap["longitude"] as Double
                            val desc = locationMap["desc"] as String

                            val location = Location(name, latitude, longitude, desc)
                            list.add(location)
                        }

                        val destQueue = DestQueue(getLoggedInAsFun().username, list)
                        destQueueList.add(destQueue)
                        break
                    }
                }

                if (destQueueList.isNotEmpty())
                {
                    if (destQueueList[0].list.isNotEmpty())
                        destQueueAdapter.setData(destQueueList[0].list)
                    else
                        showToast("No destinations in queue", this)
                }
                else Log.d("DestQueuePage", "Error retrieving queue")
            }
        }?.addOnFailureListener{
            // Handle the failure to retrieve data from the database
            Log.d("DestQueuePage", "Error loading data: $it")
        }
    }

    // Adapter to bind data to RecyclerView
    class DestQueueAdapter(username : String, context: Context) :
        RecyclerView.Adapter<DestQueueAdapter.ViewHolder>()
    {
        private var destQueueList = mutableListOf<Location>()
        private val queueManager = QueueManager.getQueueManager(username, context)
        @SuppressLint("NotifyDataSetChanged")
        fun setData(data: MutableList<Location>)
        {
            destQueueList.clear()
            destQueueList.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.dest_queue_item,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            if (destQueueList.isNotEmpty())
            {
                val destinationCard = holder.itemView.findViewById<CardView>(R.id.destCard)
                val destData = getItem(position, destQueueList)
                if (queueManager != null) holder.bind(destData, destinationCard, queueManager)
            }
        }

        override fun getItemCount(): Int { return destQueueList.size }

        private fun getItem(position: Int, queue: MutableList<Location>): Location
        {
            if (position in 0 until queue.size) return queue[position]
            return Location()
        }


        class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView)
        {
            private val refreshCallback: () -> Unit = {
                (itemView.context as DestinationQueue).refresh()
            }

            // Bind data to views in the ViewHolder
            fun bind(destData: Location, destCard : CardView, queueManager: QueueManager)
            {
                destCard.findViewById<TextView>(R.id.nameTextView).text = destData.name

                destCard.findViewById<Button>(R.id.removeButton).setOnClickListener {
                    //grab location from card and pass to function
                    queueManager.removeFromQueue(destData)
                    sleep(500)
                    refreshCallback.invoke() // call refresh after removing an item
                }
//
//                destCard.findViewById<Button>(R.id.upButton).setOnClickListener {
//                    //get position of item in queue
//                    //queueManager.reorderQueue(1, position)
//                }
//
//                destCard.findViewById<Button>(R.id.downButton).setOnClickListener {
//                    //get position of item within queue
//                    //queueManager.reorderQueue(-1, position)
//                }
            }
        }
    }

    override fun onPopupDismissed() { refresh() }
}