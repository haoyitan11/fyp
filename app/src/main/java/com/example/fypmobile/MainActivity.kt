package com.example.fypmobile

import BicepsAdapter
import ChestAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class MainActivity : AppCompatActivity() {

    private lateinit var chestText: TextView
    private lateinit var chestList: ListView
    private lateinit var chestAdapter: ChestAdapter
    private var chestCategory: MutableList<String> = mutableListOf()

    private lateinit var bicepsText: TextView
    private lateinit var bicepsList: ListView
    private lateinit var bicepsAdapter: BicepsAdapter
    private var bicepsCategory: MutableList<String> = mutableListOf()

    private lateinit var searchEditText: EditText


    private fun updateListVisibility(searchText: String) {
        when {
            searchText.contains("chest") -> {
                chestText.visibility = View.VISIBLE
                chestList.visibility = View.VISIBLE
                bicepsText.visibility = View.GONE
                bicepsList.visibility = View.GONE
                chestList.bringToFront()
            }
            searchText.contains("biceps") -> {
                chestText.visibility = View.GONE
                chestList.visibility = View.GONE
                bicepsText.visibility = View.VISIBLE
                bicepsList.visibility = View.VISIBLE
                bicepsList.bringToFront()
            }
            else -> {
                chestText.visibility = View.VISIBLE
                chestList.visibility = View.VISIBLE
                bicepsText.visibility = View.VISIBLE
                bicepsList.visibility = View.VISIBLE
            }
        }
        findViewById<FrameLayout>(R.id.frame).requestLayout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goalsetting)

        // Initialize Views
        chestText = findViewById(R.id.chestText)
        chestList = findViewById(R.id.chestList)
        bicepsText = findViewById(R.id.bicepsText)
        bicepsList = findViewById(R.id.bicepsList)
        searchEditText = findViewById(R.id.search)

        // Populate the categories for Chest and Biceps
        chestCategory.addAll(listOf("Flat Barbell Bench Press", "Incline Barbell Bench Press"))
        bicepsCategory.addAll(listOf("Barbell Curl", "Dumbbell Curl"))

        // Initialize the custom adapters with a proper layout for the items
        chestAdapter = ChestAdapter(this, android.R.layout.simple_list_item_1, chestCategory)
        bicepsAdapter = BicepsAdapter(this, android.R.layout.simple_list_item_1, bicepsCategory)

        // Set the adapters to the ListView
        chestList.adapter = chestAdapter
        bicepsList.adapter = bicepsAdapter

        chestList.setOnItemClickListener { adapterView, view, position, id ->
            val text = chestAdapter.getItem(position) // Assuming chestAdapter is an ArrayAdapter<String>
            val intent = Intent(this, GoalAdd::class.java)
            intent.putExtra("EXERCISE_NAME", text)
            startActivity(intent)
        }

        bicepsList.setOnItemClickListener { adapterView, view, position, id ->
            val text = bicepsAdapter.getItem(position) // Assuming chestAdapter is an ArrayAdapter<String>
            val intent = Intent(this, GoalAdd::class.java)
            intent.putExtra("EXERCISE_NAME", text)
            startActivity(intent)
        }

        // Add a TextWatcher to filter categories based on the entered keyword
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().toLowerCase().trim()
                updateListVisibility(searchText)
                if (searchText.isEmpty()) {
                    // Display all categories when the search keyword is empty
                    displayAllCategories()
                } else if (searchText.contains("chest")) {
                    // Filter and show only chest categories
                    filterChestCategories(searchText.replace("chest", ""))
                } else if (searchText.contains("biceps")) {
                    // Filter and show only biceps categories
                    filterBicepsCategories(searchText.replace("biceps", ""))
                } else {
                    // Hide all categories if there is no match
                    chestText.visibility = View.GONE
                    chestList.visibility = View.GONE
                    bicepsText.visibility = View.GONE
                    bicepsList.visibility = View.GONE
                }

                // Call the function to update the list visibility
                updateListVisibility(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}

            private fun filterChestCategories(keyword: String) {
                val filteredCategories =
                    chestCategory.filter { it.toLowerCase().contains(keyword.trim()) }
                chestAdapter.clear()
                chestAdapter.addAll(filteredCategories)
                chestAdapter.notifyDataSetChanged()
            }

            private fun filterBicepsCategories(keyword: String) {
                val filteredCategories =
                    bicepsCategory.filter { it.toLowerCase().contains(keyword.trim()) }
                bicepsAdapter.clear()
                bicepsAdapter.addAll(filteredCategories)
                bicepsAdapter.notifyDataSetChanged()
            }

            private fun displayAllCategories() {
                // Display Chest categories
                chestText.visibility = View.VISIBLE
                chestList.visibility = View.VISIBLE

                // Display Biceps categories
                bicepsText.visibility = View.VISIBLE
                bicepsList.visibility = View.VISIBLE
            }



            private fun updateListVisibility(searchText: String) {
                when {
                    searchText.contains("chest") -> {
                        chestText.visibility = View.VISIBLE
                        chestList.visibility = View.VISIBLE
                        bicepsText.visibility = View.GONE
                        bicepsList.visibility = View.GONE
                        chestList.bringToFront() // This will bring the chestList to the front
                    }

                    searchText.contains("biceps") -> {
                        chestText.visibility = View.GONE
                        chestList.visibility = View.GONE
                        bicepsText.visibility = View.VISIBLE
                        bicepsList.visibility = View.VISIBLE
                        bicepsList.bringToFront() // This will bring the bicepsList to the front
                    }

                    else -> {
                        // If the search text is empty or doesn't match, decide what to do here.
                        // Maybe show both, maybe show none.
                        chestText.visibility = View.VISIBLE
                        chestList.visibility = View.VISIBLE
                        bicepsText.visibility = View.VISIBLE
                        bicepsList.visibility = View.VISIBLE
                    }
                }
            }
        })
    }}
