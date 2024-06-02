package com.example.Project_Spark.repository

import android.util.Log
import com.example.Project_Spark.Meeting
import com.example.Project_Spark.Team
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getFriends(uid: String): List<Friend> {
        return try {
            // 사용자 문서 가져오기
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)

            if (user == null) {
                Log.d("FirebaseRepository", "User not found for UID: $uid")
                return emptyList()
            }

            Log.d("FirebaseRepository", "Fetched user: $user")

            // 친구 목록 가져오기
            user.friends.mapNotNull { friendUid ->
                try {
                    val friendDocument = firestore.collection("profiles").document(friendUid).get().await()
                    val friend = friendDocument.toObject(Friend::class.java)
                    Log.d("FirebaseRepository", "Fetched friend: $friend")
                    friend
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error fetching friend with UID: $friendUid", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching friends for UID: $uid", e)
            emptyList()
        }
    }

    suspend fun createTeam(team: Team) {
        firestore.collection("teams").add(team).await()
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
