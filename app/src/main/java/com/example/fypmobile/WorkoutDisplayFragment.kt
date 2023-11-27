import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fypmobile.Adapter.WorkAdapter
import com.example.fypmobile.R
import com.example.fypmobile.WorkoutSettingFragment
import com.example.fypmobile.WorkoutUpdate
import com.example.fypmobile.model.ListWorkoutDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutDisplayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutDisplayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var plusIcon: ImageView
    private lateinit var deleteIcon: ImageView
    private lateinit var filterIcon: ImageView
    private var isSortedByRecent = true
    private var selectedDocumentId: String? = null
    private val workouts: MutableList<ListWorkoutDetails.WorkoutInfo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_workoutdisplay, container, false)

        // Initialize the plusIcon ImageView and set a click listener
        plusIcon = view.findViewById(R.id.plusIcon2) // Make sure the ID matches your layout
        plusIcon.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.Constraint, WorkoutSettingFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        filterIcon = view.findViewById(R.id.filterIcon)
        filterIcon.setOnClickListener {
            isSortedByRecent = !isSortedByRecent // Toggle the sorting order
            fetchDataAndDisplay(view.findViewById(R.id.workoutRecyclerView), isSortedByRecent)
        }

        // Rest of your code...
        val recyclerView = view.findViewById<RecyclerView>(R.id.workoutRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = WorkAdapter(
            workouts,
            { documentId -> showDeleteConfirmation(documentId) }, // onDeleteClick
            { documentId -> navigateToUpdateWorkout(documentId) } // onItemSelect
        )// Set an empty adapter initially
        fetchDataAndDisplay(recyclerView, false)
        return view
    }

    private fun showDeleteConfirmation(documentId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this workout?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWorkout(documentId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteWorkout(documentId: String) {
        FirebaseFirestore.getInstance().collection("WorkoutData")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d("FirestoreData", "Document successfully deleted: $documentId")
                // Remove the workout from the list and refresh the RecyclerView
                removeWorkoutFromListAndRefreshRecyclerView(documentId)
                // Show a success message
                Toast.makeText(context, "Workout deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreData", "Error deleting document: $documentId", e)
                Toast.makeText(context, "Failed to delete document: $documentId", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeWorkoutFromListAndRefreshRecyclerView(documentId: String) {
        val index = workouts.indexOfFirst { it.documentId == documentId }
        if (index != -1) {
            workouts.removeAt(index)
            view?.findViewById<RecyclerView>(R.id.workoutRecyclerView)?.adapter?.notifyItemRemoved(index)
        }
    }

    fun onWorkoutSelected(documentId: String) {
        selectedDocumentId = documentId
    }

    private fun getSelectedWorkoutId(): String? {
        return selectedDocumentId
    }

    private fun navigateToUpdateWorkout(documentId: String) {
        val intent = Intent(activity, WorkoutUpdate::class.java)
        intent.putExtra("DOCUMENT_ID", documentId)
        startActivity(intent)
    }

    private fun setupDeleteFunctionality() {
        // Assuming you have a delete button or a menu item
        deleteIcon.setOnClickListener {
            selectedDocumentId?.let { id ->
                showDeleteConfirmation(id)
            } ?: Toast.makeText(context, "No workout selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDataAndDisplay(recyclerView: RecyclerView, isSortedByRecent: Boolean) {
        val db = FirebaseFirestore.getInstance()
        var query: com.google.firebase.firestore.Query = db.collection("WorkoutData")

        query = if (isSortedByRecent) {
            query.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
        } else {
            query.orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
        }

        query.get().addOnSuccessListener { result ->
            workouts.clear()
            for (document in result) {
                val workoutInfo = document.toObject(ListWorkoutDetails.WorkoutInfo::class.java)
                workoutInfo.documentId = document.id // Assign the document ID here
                workouts.add(workoutInfo)
            }
            recyclerView.adapter?.notifyDataSetChanged()
            if (workouts.isNotEmpty()) {
                recyclerView.adapter = WorkAdapter(
                    workouts,
                    { documentId -> showDeleteConfirmation(documentId) }, // onDeleteClick
                    { documentId -> navigateToUpdateWorkout(documentId) } // onItemSelect
                )
            } else {
                Log.d("FirestoreData", "No workout found")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WorkoutDisplayFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WorkoutDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}