package com.example.fypmobile.model

class ListWorkoutDetails {
    data class WorkoutInfo(
        val date: String? = null,
        val sets: List<SetInfo>? = null,
        val workoutName: String? = null,
        val workoutId: String? = null // Assuming this is a Long in Firestore
    ) {
        var documentId: String? = null // Mutable field for the Firestore document ID
    }

    data class SetInfo(
        val kg: String? = null,
        val reps: String? = null
    )
}