package com.example.psbnavigator.controller.appscreens
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageAdmin
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.example.psbnavigator.model.dataobjects.User
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminAddAdmin : LoggedInPageAdmin()
{

    private lateinit var editTextEmail: EditText
    private lateinit var buttonAdmin: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private val users = mutableListOf<User>()
    private var limit = 5 // Initial limit
    private var loading = false

    // Reference to the "Users" collection in Firestore
    private val userRef = DatabaseManager.getDatabaseManager()?.getUserRef()

    override fun refresh()
    {
        val go = Intent(this, AdminAddAdmin::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminaddadmin)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonAdmin.setOnClickListener { handleEventOrg() }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter with CardView items
        adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>()
        {
            // Create ViewHolder
            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): RecyclerView.ViewHolder
            {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.user_item,
                    parent,
                    false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            // Return the number of items in the list
            override fun getItemCount(): Int
            {
                return users.size
            }

            // Bind data to ViewHolder
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val locCard = holder.itemView.findViewById<CardView>(R.id.eventCard)
                val locData = getItem(position)
                bindEventData(locCard, locData)
            }
        }

        recyclerView.adapter = adapter
        loadData()
        Log.d("Admin add location", "Locations size: ${users.size}")

        // Set up scroll listener
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                // Get the last visible item position
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more data when user scrolls to the bottom
                if (!loading && lastVisibleItemPosition == totalItemCount - 1) loadMoreData()
            }
        })
    }

    private fun initializeView()
    {
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonAdmin = findViewById(R.id.buttonAdmin)
    }

    private fun handleEventOrg()
    {
        val iemail = editTextEmail.text.toString()

        //Check all users to look for a match
        userRef?.whereEqualTo("email", iemail)?.get()?.addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty) {
                showToast("No account under that email.", this)
            } else {
                changeToEventOrg(documents)
            }
        }
    }

    private fun changeToEventOrg(documents : QuerySnapshot)
    {
        documents.documents[0].reference.update("type", "Admin").addOnSuccessListener {
            showToast("The user under this email has been turned into an admin.", this)
            refresh()
        }
    }

    // Get item at a specific position
    private fun getItem(position: Int): User { return users[position] }

    // Bind event data to the CardView
    private fun bindEventData(userCard: CardView, user: User)
    {
        // Bind event data to the card view
        userCard.findViewById<TextView>(R.id.usernameTextView).text = user.username
        userCard.findViewById<TextView>(R.id.nameTextView).text = user.name
        userCard.findViewById<TextView>(R.id.emailTextView).text = user.email
        userCard.findViewById<TextView>(R.id.statusTextView).text = user.type
    }

    // Load initial data from database
    private fun loadData()
    {
        loading = true
        userRef?.limit(limit.toLong())?.get()?.addOnSuccessListener{ documents ->
            Log.d("EventPage", "Document count: ${documents.size()}")
            val newPSBUsers = mutableListOf<User>()

            for (document in documents)  // Convert database documents to PSB_Event objects
            {
                val loc = document.toObject(User::class.java)

                newPSBUsers.add(loc)

            }
            loading = false
            updateAdapterData(newPSBUsers)
        }?.addOnFailureListener{
            loading = false
            Log.e("EventPage", "Error loading data")
        }
    }

    // Load more data when the user scrolls to the bottom
    private fun loadMoreData()
    {
        limit += 5
        loadData()
    }

    // Update the adapter data
    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterData(newPSBUsers: List<User>)
    {
        users.clear()
        users.addAll(newPSBUsers)
        adapter.notifyDataSetChanged()
    }
}