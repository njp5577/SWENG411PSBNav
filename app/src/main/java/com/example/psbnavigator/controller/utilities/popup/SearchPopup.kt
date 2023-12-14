package com.example.psbnavigator.controller.utilities.popup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.SystemClock.sleep
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.model.dataobjects.Location
import com.example.psbnavigator.model.dataobjects.PSB_Event
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.example.psbnavigator.controller.utilities.managers.QueueManager

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class SearchResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val desc: String)
{
    constructor() : this("", 0.0, 0.0, "")
}

class AddToQueuePopup(private val context: Context,
                      private val dismissCallback: PopupDismissCallback
) : DetailsPopup()
{
    interface PopupDismissCallback { fun onPopupDismissed() }

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
        val searchAdapter = SearchAdapter(context, user.username, emptyList(), dismissCallback)
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        searchRecyclerView.adapter = searchAdapter

        // Set up Firebase query listener for search
        val locationRef = DatabaseManager.getDatabaseManager()?.getLocationRef()

        searchSubmitButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty())
            {
                locationRef?.get()?.addOnSuccessListener { documents ->
                        val searchResults = documents.map{
                                doc -> doc.toObject(SearchResult::class.java)
                        }.filter { result ->
                            result.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
                        }
                        searchAdapter.setData(searchResults)
                        searchRecyclerView.visibility = View.VISIBLE
                    }
                    ?.addOnFailureListener { exception ->
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
        dialog.setOnDismissListener { dismissCallback.onPopupDismissed() }
        dialog.show()
        alertDialog = dialog
    }

    class SearchAdapter(
        private val context: Context,
        private var username: String,
        private var searchResults: List<SearchResult>,
        private val dismissCallback: PopupDismissCallback
    ) : RecyclerView.Adapter<SearchAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView :View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.search_result_item,
                parent,
                false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            val result = searchResults[position]

            holder.itemView.findViewById<TextView>(R.id.nameTextView).text = result.name
            holder.itemView.findViewById<TextView>(R.id.latitudeTextView).text = "Latitude: " + result.latitude.toString()
            holder.itemView.findViewById<TextView>(R.id.longitudeTextView).text = "Longitude: " + result.longitude.toString()
            holder.itemView.findViewById<TextView>(R.id.descriptionTextView).text = result.desc

            holder.itemView.setOnClickListener {
                QueueManager.getQueueManager(username, context)?.addToQueue(
                    Location(
                        result.name,
                        result.latitude,
                        result.longitude,
                        result.desc)
                )
                sleep(500) //sleeps for 500ms to ensure destination added to queue
                dismissCallback.onPopupDismissed()
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