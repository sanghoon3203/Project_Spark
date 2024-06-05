package com.example.Project_Spark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MeetingReservation(
    val teamName: String = "",
    val date: String = "",
    val members: List<String> = emptyList()
)

class MeetingListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _meetingReservations = MutableStateFlow<List<MeetingReservation>>(emptyList())
    val meetingReservations: StateFlow<List<MeetingReservation>> = _meetingReservations

    init {
        fetchMeetingReservations()
    }

    private fun fetchMeetingReservations() {
        viewModelScope.launch {
            db.collection("MeetingReservation")
                .get()
                .addOnSuccessListener { documents ->
                    val reservations = documents.map { doc ->
                        MeetingReservation(
                            teamName = doc.getString("teamname") ?: "",
                            date = doc.getString("date") ?: "",
                            members = doc.get("members") as? List<String> ?: emptyList()
                        )
                    }
                    _meetingReservations.value = reservations
                }
        }
    }

    fun getReservationsForDate(date: LocalDate): List<MeetingReservation> {
        val formattedDate = date.toString() // Assuming the date is stored in "yyyy-MM-dd" format
        return meetingReservations.value.filter { it.date == formattedDate }
    }
}
