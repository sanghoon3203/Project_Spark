package com.example.meetingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project_Spark.Team
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDeleteViewModel @Inject constructor(
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

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            repository.deleteTeam(teamId)
            fetchTeams()
        }
    }
}
