package com.example.fypmobile

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WorkoutAdd : AppCompatActivity() {

    private var setCount = 1
    private lateinit var plusIcon: ImageView
    private lateinit var chervonLeft: ImageView
    private lateinit var exerciseNameTextView: TextView
    private lateinit var setsContainer: LinearLayout
    private lateinit var calendarView: CalendarView
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workoutadd)

        plusIcon = findViewById(R.id.plusIcon)
        chervonLeft = findViewById(R.id.chervonleft2)
        exerciseNameTextView = findViewById(R.id.workoutName)
        setsContainer = findViewById(R.id.setsContainer)
        calendarView = findViewById(R.id.workoutDate)

        val workoutName = intent.getStringExtra("EXERCISE_NAME")
        exerciseNameTextView.text = workoutName

        findViewById<Button>(R.id.addSetButton).setOnClickListener {
            addNewSet()
        }

        plusIcon.setOnClickListener {
            showConfirmationDialog()
        }

        chervonLeft.setOnClickListener {
            finish()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val currentCalendar = Calendar.getInstance()

            if (selectedCalendar.after(currentCalendar)) {
                Toast.makeText(this, "Please select today's date or a previous date", Toast.LENGTH_SHORT).show()
                calendarView.date = currentCalendar.timeInMillis // Reset to current date
            } else {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = sdf.format(selectedCalendar.time)
            }
        }

        if (selectedDate.isEmpty()) {
            // If no date is selected, use the current date
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = sdf.format(calendar.time)
        }

        addNewSet()
    }

    private fun addNewSet() {
        if (setCount < 4) {
            val setsContainer: LinearLayout = findViewById(R.id.setsContainer)

            val setLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val setText = TextView(this).apply {
                text = "Set $setCount"
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(0, 16, 16, 16) // Set padding values as needed
            }

            val kgEditText = EditText(this).apply {
                hint = "Kg"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(0, 16, 16, 16) // Set padding values as needed
            }

            val repsEditText = EditText(this).apply {
                hint = "Reps"
                inputType = InputType.TYPE_CLASS_NUMBER
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(0, 16, 16, 16) // Set padding values as needed
            }

            setLayout.addView(setText)
            setLayout.addView(kgEditText)
            setLayout.addView(repsEditText)

            setsContainer.addView(setLayout)
            setCount++
        }
    }

    private fun showConfirmationDialog() {
        val set1Layout = setsContainer.getChildAt(0) as LinearLayout
        val set1RepsEditText = set1Layout.getChildAt(2) as EditText

        val set1Reps = set1RepsEditText.text.toString()

        if (set1Reps.isEmpty()) {
            // Display Toast if set1 reps value is empty
            Toast.makeText(this, "Please write set 1 reps value", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage("Do you want to add this workout data?")
            .setPositiveButton("Yes") { _, _ ->
                saveDataToFirebase()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun saveDataToFirebase() {
        try {
            val workoutName = exerciseNameTextView.text.toString()
            val setsData = mutableListOf<Map<String, Any>>()

            for (i in 0 until setsContainer.childCount) {
                val setLayout = setsContainer.getChildAt(i) as LinearLayout
                val kgEditText = setLayout.getChildAt(1) as EditText
                val repsEditText = setLayout.getChildAt(2) as EditText

                val kg = kgEditText.text.toString()
                val reps = repsEditText.text.toString()

                // Only add the set if both kg and reps are filled out
                if (kg.isNotEmpty() && reps.isNotEmpty()) {
                    val setData = mapOf("kg" to kg, "reps" to reps)
                    setsData.add(setData)
                }
            }

            // Check if there is at least one set with data
            if (setsData.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                val documentName = "Workout${System.currentTimeMillis()}"

                // Get the current counter value from the database
                db.collection("Counters").document("workoutIdCounter")
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        var counter = documentSnapshot.getLong("counter") ?: 1000L

                        // Increment the counter
                        counter++

                        // Update the counter in the database
                        db.collection("Counters").document("workoutIdCounter")
                            .set(mapOf("counter" to counter))

                        // Create a new document with the incremented counter as workoutId
                        val workoutData = hashMapOf(
                            "workoutId" to counter.toString(), // Convert counter to a string
                            "workoutName" to workoutName,
                            "date" to selectedDate,
                            "sets" to setsData
                        )

                        db.collection("WorkoutData")
                            .document(documentName)
                            .set(workoutData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Document added with ID: $documentName")
                                Toast.makeText(this, "Workout successfully added", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error adding document", e)
                            }
                    }
            } else {
                Log.d("Firestore", "No data to write")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Exception during data writing", e)
        }
    }
}