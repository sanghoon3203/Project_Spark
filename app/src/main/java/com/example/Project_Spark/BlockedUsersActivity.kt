package com.example.Project_Spark

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.Project_Spark.ui.components.BottomNavigationBar

class BlockedUsersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlockedUsersScreen()
        }
    }
}

data class BlockedUser(val uid: String, val email: String, val name: String)

@Composable
fun BlockedUsersScreen() {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var blockedUsers by remember { mutableStateOf<List<BlockedUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = db) {
        currentUser?.let { user ->
            fetchBlockedUsers(db, user.uid, onResult = { users, error ->
                if (error != null) {
                    errorMessage = error
                } else {
                    blockedUsers = users
                }
                isLoading = false
            })
        } ?: run {
            errorMessage = "로그인이 필요합니다."
            isLoading = false
        }
    }

    fun unblockUser(uid: String) {
        currentUser?.let { user ->
            val userDocRef = db.collection("users").document(user.uid)
            userDocRef.update("blocked_users", FieldValue.arrayRemove(uid))
                .addOnSuccessListener {
                    blockedUsers = blockedUsers.filter { it.uid != uid }
                }
                .addOnFailureListener { exception ->
                    Log.e("unblockUser", "Error unblocking user: ", exception)
                    errorMessage = "사용자를 차단 해제하는 도중 오류가 발생했습니다."
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "차단된 사용자") }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage!!)
                    }
                }
                else -> {
                    BlockedUsersList(blockedUsers, ::unblockUser)
                }
            }
        }
    }
}

fun fetchBlockedUsers(
    db: FirebaseFirestore,
    userId: String,
    onResult: (List<BlockedUser>, String?) -> Unit
) {
    db.collection("users").document(userId)
        .get()
        .addOnSuccessListener { documentSnapshot ->
            val blockedUserIds = documentSnapshot.get("blocked_users") as? List<String> ?: emptyList()
            if (blockedUserIds.isEmpty()) {
                onResult(emptyList(), null)
                return@addOnSuccessListener
            }

            val users = mutableListOf<BlockedUser>()
            var fetchedCount = 0

            for (blockedUserId in blockedUserIds) {
                db.collection("profiles").document(blockedUserId)
                    .get()
                    .addOnSuccessListener { userSnapshot ->
                        val name = userSnapshot.getString("name")
                        val email = userSnapshot.getString("email")
                        if (name != null && email != null) {
                            users.add(BlockedUser(uid = blockedUserId, email = email, name = name))
                        }
                        fetchedCount++
                        if (fetchedCount == blockedUserIds.size) {
                            onResult(users, null)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("fetchBlockedUsers", "Error fetching user data: ", exception)
                        fetchedCount++
                        if (fetchedCount == blockedUserIds.size) {
                            onResult(users, null)
                        }
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("fetchBlockedUsers", "Error fetching blocked users: ", exception)
            onResult(emptyList(), "데이터를 가져오는 도중 오류가 발생했습니다.")
        }
}

@Composable
fun BlockedUsersList(users: List<BlockedUser>, onUnblock: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(users) { user ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "이메일: ${user.email}")
                Text(text = "이름: ${user.name}")
                Button(
                    onClick = { onUnblock(user.uid) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "차단 해제")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBlockedUsersScreen() {
    BlockedUsersScreen()
}
