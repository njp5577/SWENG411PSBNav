//DestinationQueue.kt
package com.example.loginpageassignment.appscreens
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.parentpageclasses.LoggedInPage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DestQueue(var user: String, var list: MutableList<Location>)
{
    constructor() : this("", mutableListOf())
}

/*
TODO:
    Add locations to tester (tester, password) user's queue and test view
    Add default text for when no locations in queue
    Add to queue button functionality
    Remove from queue button functionality
    move up/down button functionality
    test scroll capabilities
    possibly extract adapter to its own class and set up generalization relationship with event adapter
*/
class DestinationQueue : LoggedInPage()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var destQueueAdapter: DestQueueAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinationqueue)

        recyclerView = findViewById(R.id.destQueueRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        destQueueAdapter = DestQueueAdapter()
        recyclerView.adapter = destQueueAdapter

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val queueRef = firestore.collection("Queues")

        queueRef.get().addOnSuccessListener { documents ->
            if (documents.isEmpty)
            {
                // Handle the case where there's no data in the database
                Log.e("DestQueuePage", "No documents")
            }
            else
            {
                val destQueueList = mutableListOf<DestQueue>()

                //find user's queue
                for (document in documents)
                {
                    if (document.getString("username") == getLoggedInAsFun().username)
                    {
                        val destQueue = document.toObject(DestQueue::class.java)
                        destQueueList.add(destQueue)
                        break
                    }
                }

                if(destQueueList.isNotEmpty())
                {
                    if (destQueueList[0].list.isNotEmpty())
                        destQueueAdapter.setData(destQueueList[0].list)
                    else
                        Toast.makeText(this, "No destinations in queue", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Log.d("DestQueuePage", "Error retrieving queue")
                }
            }
        }.addOnFailureListener{
            // Handle the failure to retrieve data from the database
            Log.e("DestQueuePage", "Error loading data: $it")
        }
    }

    // Adapter to bind data to RecyclerView
    class DestQueueAdapter : RecyclerView.Adapter<DestQueueAdapter.ViewHolder>()
    {
        private var destQueueList = mutableListOf<Location>()
        @SuppressLint("NotifyDataSetChanged")
        fun setData(data: List<Location>)
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
                holder.bind(destData, destinationCard)
            }
        }

        override fun getItemCount(): Int
        {
            return destQueueList.size
        }

        private fun getItem(position: Int, queue: MutableList<Location>): Location
        {
            if (position in 0 until queue.size) return queue[position]
            return Location()
        }


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            // Bind data to views in the ViewHolder
            fun bind(destData: Location, destCard : CardView)
            {
                destCard.findViewById<TextView>(R.id.nameTextView).text = destData.name
            }
        }
    }
}

//    private lateinit var buttonAdd: Button
//    private lateinit var buttonRetrieve: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_destinationqueue)
//
//        buttonAdd = findViewById(R.id.buttonAdd)
//        buttonRetrieve = findViewById(R.id.buttonRetrieve)
//
//        val userLogin = intent.getStringExtra("User")
//        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
//        setLoggedInAsFun(user)
//
//        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//        val queueRef = firestore.collection("Queues")
//
//        buttonAdd.setOnClickListener {
//            val list = mutableListOf(Location("Name1",1.5, 1.1,"Desc1"), Location("Name2",12.5, 13.5, "Desc2"))
//
//            queueRef.add(DestQueue(list)).addOnSuccessListener {
//                Toast.makeText(this, "Added ${DestQueue(list)}!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        buttonRetrieve.setOnClickListener {
//            queueRef.get().addOnSuccessListener{ documents ->
//                if ((documents.isEmpty)) {
//                    Toast.makeText(this, "No list in DB", Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    Toast.makeText(this, "Retrieved ${documents.documents[0].get("list")}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }