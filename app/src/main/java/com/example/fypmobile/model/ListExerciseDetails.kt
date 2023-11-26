package com.example.fypmobile.model

class ListExerciseDetails {
    data class ExerciseInfo(
        val name: String = "",
        val date: String = "",
        val sets: List<SetInfo> = listOf()
    )

    data class SetInfo(
        val kg: String = "",
        val reps: String = ""
    )
}