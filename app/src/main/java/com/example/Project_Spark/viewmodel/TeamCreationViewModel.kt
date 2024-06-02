package com.example.Project_Spark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project_Spark.Team
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.repository.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamCreateViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {
    private val _friendsList = MutableStateFlow<List<Friend>>(emptyList())
    val friendsList: StateFlow<List<Friend>> = _friendsList

    fun fetchFriends() {
        viewModelScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            val friends = repository.getFriends(uid)
            _friendsList.value = friends
        }
    }

    fun createTeam(teamName: String, department: String, teamDescription: String, teamMembers: List<String>) {
        viewModelScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            val team = Team(
                name = teamName,
                department = department,
                description = teamDescription,
                members = listOf(uid) + teamMembers
            )
            repository.createTeam(team)
        }
    }
}
