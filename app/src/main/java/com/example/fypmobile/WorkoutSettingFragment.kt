package com.example.fypmobile

import BicepsAdapter
import ChestAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutSettingFragment : Fragment() {

    private lateinit var chestText: TextView
    private lateinit var chestList: ListView
    private lateinit var chestAdapter: ChestAdapter
    private lateinit var bicepsText: TextView
    private lateinit var bicepsList: ListView
    private lateinit var bicepsAdapter: BicepsAdapter
    private lateinit var searchEditText: EditText
    private var chestCategory: MutableList<String> = mutableListOf()
    private var bicepsCategory: MutableList<String> = mutableListOf()
    private lateinit var chervonLeft: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workoutset, container, false)

        chervonLeft = view.findViewById(R.id.chervonleft) // Make sure the ID matches your layout
        chervonLeft.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Initialize Views
        chestText = view.findViewById(R.id.chestText)
        chestList = view.findViewById(R.id.chestList)
        bicepsText = view.findViewById(R.id.bicepsText)
        bicepsList = view.findViewById(R.id.bicepsList)
        searchEditText = view.findViewById(R.id.search)
        // ... other initializations

        // Populate the categories for Chest and Biceps
        chestCategory.addAll(listOf("Flat Barbell Bench Press", "Incline Barbell Bench Press"))
        bicepsCategory.addAll(listOf("Barbell Curl", "Dumbbell Curl"))

        // Initialize the custom adapters
        chestAdapter = ChestAdapter(requireContext(), android.R.layout.simple_list_item_1, chestCategory)
        bicepsAdapter = BicepsAdapter(requireContext(), android.R.layout.simple_list_item_1, bicepsCategory)

        // Set the adapters to the ListView
        chestList.adapter = chestAdapter
        bicepsList.adapter = bicepsAdapter

        // Set OnItemClickListener for ListView
        setupListViewClickListeners()

        // Add TextWatcher for search functionality
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
        })

        return view
    }

    private fun setupListViewClickListeners() {
        chestList.setOnItemClickListener { _, _, position, _ ->
            val text = chestAdapter.getItem(position)
            val intent = Intent(activity, WorkoutAdd::class.java)
            intent.putExtra("EXERCISE_NAME", text)
            startActivity(intent)
        }

        bicepsList.setOnItemClickListener { _, _, position, _ ->
            val text = bicepsAdapter.getItem(position)
            val intent = Intent(activity, WorkoutAdd::class.java)
            intent.putExtra("EXERCISE_NAME", text)
            startActivity(intent)
        }
    }

    // Implement updateListVisibility, filterChestCategories, filterBicepsCategories, and displayAllCategories
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

    // ... similar to the ExerciseSetting class
    companion object {
        fun newInstance(): WorkoutSettingFragment {
            return WorkoutSettingFragment()
        }
    }


}