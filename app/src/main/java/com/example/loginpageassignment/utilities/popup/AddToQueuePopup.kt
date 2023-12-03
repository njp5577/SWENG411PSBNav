package com.example.loginpageassignment.utilities.popup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.dataobjects.PSB_Event
import com.example.loginpageassignment.utilities.queue.QueueManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val desc: String)
{
    constructor() : this("", 0.0, 0.0, "")
}

class AddToQueuePopup(private val context: Context) : DetailsPopup()
{
    private var alertDialog: AlertDialog? = null

    override fun showDetails(event: PSB_Event, user: CurrentUser)
    {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.add_to_queue_popup, null)
        dialogBuilder.setView(dialogView)

        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val searchSubmitButton = dialogView.findViewById<Button>(R.id.submit_button)
        val searchRecyclerView = dialogView.findViewById<RecyclerView>(R.id.searchRecyclerView)

        // Set up RecyclerView with an empty list initially
        val searchAdapter = SearchAdapter(user.username, emptyList()) { alertDialog?.dismiss()}
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchRecyclerView.adapter = searchAdapter

        // Set up Firebase query listener for search
        val locationRef = FirebaseFirestore.getInstance().collection("Locations")

        searchSubmitButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty())
            {
                locationRef.whereEqualTo("name", query).get()
                    .addOnSuccessListener { documents ->
                        val searchResults = documents.map{
                                doc -> doc.toObject(SearchResult::class.java)
                        }
                        searchAdapter.setData(searchResults)
                        searchRecyclerView.visibility = View.VISIBLE
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SearchPopup", "Error getting search results", exception)
                    }
            }
            else
            {
                // Clear search results and hide RecyclerView when search query is empty
                searchAdapter.setData(emptyList())
                searchRecyclerView.visibility = View.GONE
            }
        }

        val dialog = dialogBuilder.create()
        dialog.show()
        alertDialog = dialog
    }

    class SearchAdapter(
        private var username : String,
        private var searchResults : List<SearchResult>,
        private val dismissCallback: () -> Unit) :
        RecyclerView.Adapter<SearchAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView :View) : RecyclerView.ViewHolder(itemView){}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder
        {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.search_result_item,
                parent,
                false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int)
        {
            val result = searchResults[position]

            holder.itemView.findViewById<TextView>(R.id.nameTextView).text = result.name
            holder.itemView.findViewById<TextView>(R.id.latitudeTextView).text = "Latitude: " + result.latitude.toString()
            holder.itemView.findViewById<TextView>(R.id.longitudeTextView).text = "Longitude: " + result.longitude.toString()
            holder.itemView.findViewById<TextView>(R.id.descriptionTextView).text = result.desc

            holder.itemView.setOnClickListener {
                QueueManager.getQueueManager(username)?.addToQueue(
                    Location(
                        result.name,
                        result.latitude,
                        result.longitude,
                        result.desc))

                dismissCallback.invoke();
            }
        }

        override fun getItemCount(): Int { return searchResults.size }

        // method to update the data in the adapter
        @SuppressLint("NotifyDataSetChanged")
        fun setData(newData: List<SearchResult>)
        {
            searchResults = newData
            notifyDataSetChanged()
        }
    }
}