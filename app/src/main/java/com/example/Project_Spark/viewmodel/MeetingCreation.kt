package com.example.meetingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project_Spark.Meeting
import com.example.Project_Spark.Team
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MeetingCreateViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    init {
        fetchTeams()
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            val teams = repository.getTeamsByUid(uid)
            _teams.value = teams
        }
    }

    fun createMeeting(teamId: String?, date: LocalDate) {
        viewModelScope.launch {
            if (teamId == null) return@launch
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            val team = _teams.value.find { it.id == teamId } ?: return@launch
            val meeting = Meeting(
                date = date.toString(),
                teamName = team.name,
                members = team.members
            )
            repository.createMeeting(meeting)
        }
    }
}
