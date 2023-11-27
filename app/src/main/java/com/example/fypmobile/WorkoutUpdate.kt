package com.example.fypmobile

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fypmobile.model.ListWorkoutDetails
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WorkoutUpdate : AppCompatActivity() {

    // Define UI elements
    private lateinit var workoutNameTextView: TextView
    private lateinit var workoutDateTextView: TextView
    private lateinit var repRecordTextView: TextView
    private lateinit var maxRepRecordTextView: TextView
    private lateinit var weightRecordTextView: TextView
    private lateinit var maxWeightRecordTextView: TextView
    private lateinit var setsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workoutupdate)

        // Initialize your UI elements
        initializeUI()

        // Get the document ID passed from the previous activity
        val documentId = intent.getStringExtra("DOCUMENT_ID") ?: return

        val updateIcon = findViewById<ImageView>(R.id.updateIcon)
        updateIcon.setOnClickListener {
            showUpdateConfirmationDialog()
        }

        // Fetch document data from Firestore
        fetchDocumentData(documentId)
    }

    private fun showUpdateConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Update Workout")
            .setMessage("Do you want to update this workout data?")
            .setPositiveButton("Yes") { _, _ ->
                updateWorkoutData()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateWorkoutData() {
        val documentId = intent.getStringExtra("DOCUMENT_ID") ?: return
        val db = FirebaseFirestore.getInstance()

        val updatedSets = mutableListOf<ListWorkoutDetails.SetInfo>()
        for (i in 0 until setsContainer.childCount) {
            val setLayout = setsContainer.getChildAt(i) as LinearLayout
            val kgEditText = setLayout.getChildAt(1) as EditText
            val repsEditText = setLayout.getChildAt(2) as EditText

            val kg = kgEditText.text.toString()
            val reps = repsEditText.text.toString()
            if (kg.isNotEmpty() && reps.isNotEmpty()) {
                updatedSets.add(ListWorkoutDetails.SetInfo(kg, reps))
            }
        }

        val updatedData = hashMapOf<String, Any>(
            "sets" to updatedSets,
            // Include other fields you want to update
        )

        db.collection("WorkoutData").document(documentId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Workout updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity, or navigate as needed
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating document", e)
                Toast.makeText(this, "Failed to update workout", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initializeUI() {
        workoutNameTextView = findViewById(R.id.workUpdName)
        workoutDateTextView = findViewById(R.id.workUpdDate)
        repRecordTextView = findViewById(R.id.repRecord)
        maxRepRecordTextView = findViewById(R.id.maxRepRecord)
        weightRecordTextView = findViewById(R.id.weightRecord)
        maxWeightRecordTextView = findViewById(R.id.maxWeightRecord)
        setsContainer = findViewById(R.id.setsContainer2)

        // Set on-click listener to go back
        findViewById<ImageView>(R.id.chervonleft3).setOnClickListener {
            finish()
        }
    }

    private fun fetchDocumentData(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("WorkoutData").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    updateUIWithDocumentData(document)
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents: ", e)
            }
    }

    private fun updateUIWithDocumentData(document: DocumentSnapshot) {
        val workoutInfo = document.toObject(ListWorkoutDetails.WorkoutInfo::class.java)
        workoutInfo?.let {
            workoutNameTextView.text = it.workoutName
            workoutDateTextView.text = it.date

            updateSetsContainer(it.sets)
            updateRecords(it.sets, it.workoutName ?: "") // Pass workout name
        }
    }

    private fun updateSetsContainer(sets: List<ListWorkoutDetails.SetInfo>?) {
        setsContainer.removeAllViews() // Clear any existing views

        // Ensure three sets are displayed
        val numberOfSets = 3
        for (index in 0 until numberOfSets) {
            val set = sets?.getOrNull(index)

            val setLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val setText = TextView(this).apply {
                text = "Set ${index + 1}"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 16, 16, 16)
            }

            val kgEditText = EditText(this).apply {
                hint = "Kg"
                setText(set?.kg ?: "")
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(0, 16, 16, 16)
                gravity = Gravity.CENTER
            }

            val repsEditText = EditText(this).apply {
                hint = "Reps"
                setText(set?.reps ?: "")
                inputType = InputType.TYPE_CLASS_NUMBER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(0, 16, 16, 16)
                gravity = Gravity.CENTER
            }

            setLayout.addView(setText)
            setLayout.addView(kgEditText)
            setLayout.addView(repsEditText)

            setsContainer.addView(setLayout)
        }
    }

    private fun updateRecords(sets: List<ListWorkoutDetails.SetInfo>?, workoutName: String) {
        sets?.let {
            val highestRep = sets.maxOfOrNull { it.reps?.toIntOrNull() ?: 0 } ?: 0
            val highestWeight = sets.maxOfOrNull { it.kg?.toFloatOrNull() ?: 0f } ?: 0f

            repRecordTextView.text = "Highest Rep: $highestRep"
            weightRecordTextView.text = "Highest Weight: $highestWeight"

            fetchAndDisplayMaxRecords(workoutName) // Use the workout name
        }
    }

    private fun fetchAndDisplayMaxRecords(workoutName: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("WorkoutData").get()
            .addOnSuccessListener { result ->
                var maxRep = 0
                var maxWeight = 0f

                for (document in result) {
                    val workoutInfo = document.toObject(ListWorkoutDetails.WorkoutInfo::class.java)
                    if (workoutInfo.workoutName == workoutName) {
                        workoutInfo.sets?.forEach { set ->
                            maxRep = maxOf(maxRep, set.reps?.toIntOrNull() ?: 0)
                            maxWeight = maxOf(maxWeight, set.kg?.toFloatOrNull() ?: 0f)
                        }
                    }
                }

                maxRepRecordTextView.text = "Max Rep Record: $maxRep"
                maxWeightRecordTextView.text = "Max Weight Record: $maxWeight"
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents: ", e)
            }
    }


    // Additional methods to handle editing and saving back to Firestore...
}