package com.example.meetingapp

import com.example.Project_Spark.Meeting
import com.example.Project_Spark.Team
import com.example.Project_Spark.User
import com.example.Project_Spark.model.Friend
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getFriends(uid: String): List<Friend> {
        return firestore.collection("users").document(uid).get().await()
            .toObject(User::class.java)?.friends?.mapNotNull { friendUid ->
                firestore.collection("profile").document(friendUid).get().await()
                    .toObject(Friend::class.java)
            } ?: emptyList()
    }

    suspend fun createTeam(team: Team) {
        firestore.collection("teams").document(team.id).set(team).await()
    }

    suspend fun getTeamsByUid(uid: String): List<Team> {
        return firestore.collection("teams").whereArrayContains("members", uid).get().await()
            .toObjects(Team::class.java)
    }

    suspend fun deleteTeam(teamId: String) {
        firestore.collection("teams").document(teamId).delete().await()
    }

    suspend fun createMeeting(meeting: Meeting) {
        val docId = "${meeting.date}-${meeting.teamName}"
        firestore.collection("meeting_reservation").document(docId).set(meeting).await()
    }
}
