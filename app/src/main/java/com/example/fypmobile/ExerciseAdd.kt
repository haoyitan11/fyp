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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ExerciseAdd : AppCompatActivity() {

    private var setCount = 1
    private lateinit var plusIcon: ImageView
    private lateinit var chervonLeft: ImageView
    private lateinit var exerciseNameTextView: TextView
    private lateinit var setsContainer: LinearLayout
    private lateinit var calendarView: CalendarView
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exerciseadd)

        plusIcon = findViewById(R.id.plusIcon)
        chervonLeft = findViewById(R.id.chervonleft2)
        exerciseNameTextView = findViewById(R.id.exerciseName)
        setsContainer = findViewById(R.id.setsContainer)
        calendarView = findViewById(R.id.exerciseDate)

        val exerciseName = intent.getStringExtra("EXERCISE_NAME")
        exerciseNameTextView.text = exerciseName

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
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = sdf.format(calendar.time)
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
        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage("Do you want to add this exercise data?")
            .setPositiveButton("Yes") { _, _ ->
                saveDataToFirebase()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun saveDataToFirebase() {
        try {
            val exerciseName = exerciseNameTextView.text.toString()
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

                // Create a new document with a dynamic name
                val documentName = "Exercise${System.currentTimeMillis()}"
                val exerciseData = hashMapOf(
                    "exerciseName" to exerciseName,
                    "date" to selectedDate,
                    "sets" to setsData
                )

                db.collection("ExerciseData")
                    .document(documentName)
                    .set(exerciseData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "DocumentSnapshot added with ID: $documentName")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding document", e)
                    }
            } else {
                Log.d("Firestore", "No data to write")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Exception during data writing", e)
        }
    }
}