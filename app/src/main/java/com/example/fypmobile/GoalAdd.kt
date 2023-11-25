package com.example.fypmobile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GoalAdd : AppCompatActivity() {

    private var setCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goaladd)

        val imageView: ImageView = findViewById(R.id.chervonleft2)
        val exerciseName = intent.getStringExtra("EXERCISE_NAME")
        val textView = findViewById<TextView>(R.id.exerciseName) // ID of your TextView in goaladd.xml
        textView.text = exerciseName

        val addSetButton: Button = findViewById(R.id.addSetButton)
        addSetButton.setOnClickListener {
            addNewSet()
        }

        addNewSet()

        imageView.setOnClickListener {
            // Finish the current activity and go back to the previous one (GoalSetting)
            finish()
        }
    }

    private fun addNewSet() {
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
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(0, 16, 16, 16) // Set padding values as needed
        }

        val kgEditText = EditText(this).apply {
            hint = "Kg"
            inputType = InputType.TYPE_CLASS_NUMBER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(0, 16, 16, 16) // Set padding values as needed
        }

        val repsEditText = EditText(this).apply {
            hint = "Reps"
            inputType = InputType.TYPE_CLASS_NUMBER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(0, 16, 16, 16) // Set padding values as needed
        }

        setLayout.addView(setText)
        setLayout.addView(kgEditText)
        setLayout.addView(repsEditText)

        setsContainer.addView(setLayout)
        setCount++
    }
}