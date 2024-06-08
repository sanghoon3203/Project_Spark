package com.example.Project_Spark

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ConfirmedMeeting(
    val meetingDate: String = "",
    val meetingTeamName: String = "",
    val members: List<String> = emptyList(),
    val teamName: String = "",
    val userId: String = "",
    val comments: List<String> = emptyList()
)

class ConfirmedMeetingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                ConfirmedMeetingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmedMeetingsScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val meetings = remember { mutableStateListOf<ConfirmedMeeting>() }

    LaunchedEffect(Unit) {
        try {
            val meetingCollection = firestore.collection("confirmed_meetings").get().await()
            for (doc in meetingCollection) {
                val meeting = doc.toObject(ConfirmedMeeting::class.java)
                if (meeting.userId == uid || meeting.members.contains(uid)) {
                    meetings.add(meeting)
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error loading meetings", e)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "확정 미팅", fontFamily = FontFamily(Font(R.font.applesdgothicneobold))) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable {
                            // HomeActivity_meeting 이동 로직
                        }
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(meetings) { meeting ->
                        ConfirmedMeetingItem(meeting)
                    }
                }
            }
        }
    )
}

@Composable
fun ConfirmedMeetingItem(meeting: ConfirmedMeeting) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        CommentDialog(meeting = meeting, onDismiss = { showDialog = false })
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${meeting.meetingDate}/${meeting.meetingTeamName}팀 ${meeting.teamName}의 미팅 예약!",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CommentDialog(meeting: ConfirmedMeeting, onDismiss: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    var comment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var comments by remember { mutableStateOf(meeting.comments.joinToString("\n")) }

    LaunchedEffect(meeting) {
        scope.launch {
            val loadedComments = loadComments(meeting)
            comments = loadedComments.joinToString("\n")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("미팅 게시판") },
        text = {
            Column {
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    item {
                        Text(comments)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("댓글 작성") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    addComment(meeting, comment)
                    comment = ""
                    val updatedComments = loadComments(meeting)
                    comments = updatedComments.joinToString("\n")
                }
            }) {
                Text("작성")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

suspend fun addComment(meeting: ConfirmedMeeting, comment: String) {
    val firestore = FirebaseFirestore.getInstance()
    try {
        val meetingDocId = "${meeting.meetingDate}-${meeting.meetingTeamName}-${meeting.teamName}"
        val meetingDoc = firestore.collection("confirmed_meetings").document(meetingDocId).get().await()
        val currentComments = meetingDoc.get("comments") as? List<String> ?: emptyList()
        val updatedComments = currentComments + comment
        firestore.collection("confirmed_meetings").document(meetingDocId)
            .update("comments", updatedComments)
            .await()
    } catch (e: Exception) {
        Log.e("Firestore", "Error adding comment", e)
    }
}

suspend fun loadComments(meeting: ConfirmedMeeting): List<String> {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val meetingDocId = "${meeting.meetingDate}-${meeting.meetingTeamName}-${meeting.teamName}"
        val meetingDoc = firestore.collection("confirmed_meetings").document(meetingDocId).get().await()
        meetingDoc.get("comments") as? List<String> ?: emptyList()
    } catch (e: Exception) {
        Log.e("Firestore", "Error loading comments", e)
        emptyList()
    }
}


