// /mnt/data/MeetingListViewModel.kt
package com.example.Project_Spark.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project_Spark.Meeting
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class MeetingListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _meetingReservations = MutableStateFlow<List<Meeting>>(emptyList())
    val meetingReservations: StateFlow<List<Meeting>> = _meetingReservations

    init {
        fetchMeetingReservations()
    }

    private fun fetchMeetingReservations() {
        viewModelScope.launch {
            try {
                val documents = db.collection("MeetingReservation").get().await()
                val reservations = documents.map { doc ->
                    Meeting(
                        teamName = doc.getString("teamname") ?: "",
                        date = doc.getString("date") ?: "",
                        members = doc.get("members") as? List<String> ?: emptyList()
                    )
                }
                _meetingReservations.value = reservations
            } catch (e: Exception) {
                // 에러 처리 로직 추가
            }
        }
    }

    fun getUserTeams(userId: String) = flow {
        val documents = db.collection("teams")
            .whereArrayContains("members", userId)
            .get()
            .await()
        val teams = documents.map { doc ->
            com.example.Project_Spark.model.Team(
                id = doc.id,
                name = doc.getString("name") ?: "",
                members = doc.get("members") as? List<String> ?: emptyList()
            )
        }
        emit(teams)
    }

    fun confirmMatching(reservation: Meeting, context: Context) {
        val matchingId = UUID.randomUUID().toString()
        val matchingData = mapOf(
            "teamName" to reservation.teamName,
            "date" to reservation.date,
            "members" to reservation.members,
            "matchingId" to matchingId
        )

        viewModelScope.launch {
            try {
                db.collection("meeting_fixing")
                    .document(reservation.date)
                    .collection("Matchings")
                    .document(matchingId)
                    .set(matchingData)
                    .await()
                db.collection("MeetingReservation")
                    .document(reservation.date)
                    .delete()
                    .await()
                Toast.makeText(context, "매칭이 확정되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "매칭 확정에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
