package com.example.fypmobile.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fypmobile.R
import com.example.fypmobile.model.ListExerciseDetails

class ExerciseAdapter(private val exercises: List<ListExerciseDetails.ExerciseInfo>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView? = itemView.findViewById(R.id.exerciseName)
        val dateTextView: TextView? = itemView.findViewById(R.id.exerciseDate)
        // Initialize other views
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.nameTextView?.text = exercise.name
        holder.dateTextView?.text = exercise.date
        // Bind other data to views
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}