package com.example.Project_Spark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

data class MeetingReservation(val date: String, val teamName: String, val members: List<String>)

class MeetingWaitingRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                MeetingWaitingRoomScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingWaitingRoomScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val meetings = remember { mutableStateListOf<MeetingReservation>() }

    LaunchedEffect(Unit) {
        try {
            val meetingCollection = firestore.collection("meeting_reservation").get().await()
            for (doc in meetingCollection) {
                val members = doc.get("members") as List<String>? ?: emptyList()
                meetings.add(
                    MeetingReservation(
                        date = doc.id.split("-").take(3).joinToString("-"),
                        teamName = doc.id.split("-").last(),
                        members = members
                    )
                )
            }
        } catch (e: Exception) {
            // Handle exceptions, e.g., log error or show message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "미팅대기실",fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable {
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
                        MeetingItem(meeting)
                    }
                }
            }
        }
    )
}



@Composable
fun MeetingItem(meeting: MeetingReservation) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf("") }
    val userTeams = remember { mutableStateListOf<String>() }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val teams = firestore.collection("teams").whereArrayContains("members", currentUser.uid).get().await()
                    userTeams.clear()
                    for (team in teams) {
                        team.getString("teamName")?.let {
                            userTeams.add(it)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., log error or show message
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("팀 선택") },
            text = {
                Column {
                    userTeams.forEach { team ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedTeam = team }
                        ) {
                            RadioButton(
                                selected = selectedTeam == team,
                                onClick = { selectedTeam = team }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(team)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    // 신청 확인 팝업 로직
                }) {
                    Text("신청",fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소",fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),)
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val profileImageUrl = remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                try {
                    val profileDoc = firestore.collection("profiles").document(meeting.members.firstOrNull() ?: "").get().await()
                    profileImageUrl.value = profileDoc.getString("imageUrl") ?: ""
                } catch (e: Exception) {
                    // Handle exceptions, e.g., log error or show message
                }
            }

            Image(
                painter = rememberImagePainter(profileImageUrl.value),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.White, shape = CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(meeting.teamName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(meeting.date)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { showDialog = true }) {
                Text("신청")
            }
        }
    }
}

@Composable
fun ConfirmPopup(selectedTeam: String, meeting: MeetingReservation, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("미팅 신청 확인") },
        text = { Text("${selectedTeam}에게 ${meeting.date}에 미팅을 신청하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("신청")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MeetingWaitingRoomScreenPreview() {
    ProjectSparkTheme {
        MeetingWaitingRoomScreen()
    }
}
