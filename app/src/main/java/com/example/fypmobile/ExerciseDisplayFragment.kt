package com.example.fypmobile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fypmobile.Adapter.ExerciseAdapter
import com.example.fypmobile.model.ListExerciseDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseDisplayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExerciseDisplayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercisedisplay, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.exerciseRecyclerView)

        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchDataAndDisplay(recyclerView)

        return view
    }

    private fun fetchDataAndDisplay(recyclerView: RecyclerView) {
        val database = FirebaseDatabase.getInstance().getReference("ExerciseData")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exercises = mutableListOf<ListExerciseDetails.ExerciseInfo>()
                snapshot.children.forEach { data ->
                    val exerciseInfo = data.getValue(ListExerciseDetails.ExerciseInfo::class.java)
                    Log.d("FirebaseData", "Exercise: $exerciseInfo")
                    exerciseInfo?.let { exercises.add(it) }
                }
                if (exercises.isNotEmpty()) {
                    recyclerView.adapter = ExerciseAdapter(exercises)
                } else {
                    Log.d("FirebaseData", "No exercises found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExerciseDisplayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExerciseDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}