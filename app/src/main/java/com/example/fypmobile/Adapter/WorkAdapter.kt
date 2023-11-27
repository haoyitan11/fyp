package com.example.fypmobile.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fypmobile.R
import com.example.fypmobile.model.ListWorkoutDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WorkAdapter(
    val workouts: MutableList<ListWorkoutDetails.WorkoutInfo>,
    private val onDeleteClick: (String) -> Unit,
    private val onItemClick: (String) -> Unit // Add this line
) :
    RecyclerView.Adapter<WorkAdapter.WorkoutViewHolder>() {
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWorkoutDate: TextView = itemView.findViewById(R.id.tvWorkoutDate)
        val tvWorkoutName: TextView = itemView.findViewById(R.id.tvWorkoutName)
        val set1Kg: TextView = itemView.findViewById(R.id.set1Kg)
        val set1Rep: TextView = itemView.findViewById(R.id.set1Rep)
        val set2Kg: TextView = itemView.findViewById(R.id.set2Kg)
        val set2Rep: TextView = itemView.findViewById(R.id.set2Rep)
        val set3Kg: TextView = itemView.findViewById(R.id.set3Kg)
        val set3Rep: TextView = itemView.findViewById(R.id.set3Rep)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)




    }


        // ... Initialize other TextViews for sets

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.tvWorkoutDate.text = workout.date ?: "--"
        holder.tvWorkoutName.text = workout.workoutName ?: "--"

        val formattedDate = formatDate(workout.date)
        holder.tvWorkoutDate.text = formattedDate

        holder.deleteIcon.setOnClickListener {
            val documentId = workout.documentId ?: return@setOnClickListener
            onDeleteClick(documentId)
        }

        holder.itemView.setOnClickListener {
            workout.documentId?.let { it1 -> onItemClick(it1) } // Call the listener with the document ID
        }

        workout.sets?.let { sets ->
            holder.set1Kg.text = sets.getOrNull(0)?.kg ?: "--"
            holder.set1Rep.text = sets.getOrNull(0)?.reps ?: "--"
            holder.set2Kg.text = sets.getOrNull(1)?.kg ?: "--"
            holder.set2Rep.text = sets.getOrNull(1)?.reps ?: "--"
            holder.set3Kg.text = sets.getOrNull(2)?.kg ?: "--"
            holder.set3Rep.text = sets.getOrNull(2)?.reps ?: "--"
            // Continue for as many sets as you need
        } ?: run {
            // If sets is null, set all to "--"
            holder.set1Kg.text = "--"
            holder.set1Rep.text = "--"
            holder.set2Kg.text = "--"
            holder.set2Rep.text = "--"
            holder.set3Kg.text = "--"
            holder.set3Rep.text = "--"
        }
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return "--"

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = sdf.parse(dateStr)
        val currentDate = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            sdf.format(currentDate.time) == dateStr -> "Today"
            sdf.format(yesterday.time) == dateStr -> "Yesterday"
            else -> dateStr
        }
    }


    override fun getItemCount(): Int {
        return workouts.size
    }
}
