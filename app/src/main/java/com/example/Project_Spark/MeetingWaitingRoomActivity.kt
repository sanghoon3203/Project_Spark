// /mnt/data/MeetingWaitingRoomActivity.kt
package com.example.Project_Spark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.Meeting
import com.example.Project_Spark.viewmodel.MeetingListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.android.exception.SendbirdException
import com.sendbird.uikit.activities.ChannelActivity
import kotlinx.coroutines.launch

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

@Composable
fun MeetingWaitingRoomScreen(viewModel: MeetingListViewModel = viewModel()) {
    val context = LocalContext.current
    var showTeamSelectionDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Meeting?>(null) }
    var selectedTeamId by remember { mutableStateOf<String?>(null) }
    var selectedMeeting by remember { mutableStateOf<Meeting?>(null) }
    val reservations by viewModel.meetingReservations.collectAsState()
    val confirmedMeetings = remember { mutableStateListOf<Meeting>() }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(reservations) { reservation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("팀 이름: ${reservation.teamName}")
                        Text("날짜: ${reservation.date}")
                        Text("멤버: ${reservation.members.joinToString(", ")}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            selectedReservation = reservation
                            showTeamSelectionDialog = true
                        }) {
                            Text("매칭")
                        }
                    }
                }
            }
        }

        if (showTeamSelectionDialog) {
            TeamSelectionDialog(
                onDismiss = { showTeamSelectionDialog = false },
                onConfirm = { teamId ->
                    selectedTeamId = teamId
                    showTeamSelectionDialog = false
                    showConfirmationDialog = true
                },
                userId = viewModel.currentUserId
            )
        }

        if (showConfirmationDialog) {
            ConfirmationDialog(
                reservation = selectedReservation!!,
                teamId = selectedTeamId!!,
                onConfirm = {
                    viewModel.confirmMatching(selectedReservation!!, context)
                    confirmedMeetings.add(selectedReservation!!)
                    showConfirmationDialog = false
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }

        LazyColumn {
            items(confirmedMeetings) { meeting ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("팀 이름: ${meeting.teamName}")
                        Text("예약 날짜: ${meeting.date}")
                        Button(onClick = {
                            selectedMeeting = meeting
                            showChatDialog = true
                        }) {
                            Text("채팅")
                        }
                    }
                }
            }
        }

        if (showChatDialog) {
            ChatDialog(
                meeting = selectedMeeting!!,
                onDismiss = { showChatDialog = false },
                onStartChat = { teamName, members ->
                    initializeSendbird(context, FirebaseAuth.getInstance().currentUser?.uid ?: "") {
                        createChannel(members, context)
                    }
                    showChatDialog = false
                }
            )
        }

        BottomNavigationBar()
    }
}

@Composable
fun TeamSelectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    userId: String,
    viewModel: MeetingListViewModel = viewModel()
) {
    val teams by viewModel.getUserTeams(userId).collectAsState(initial = emptyList())
    var selectedTeam by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("팀 선택") },
        text = {
            Column {
                teams.forEach { team ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedTeam == team.id,
                            onClick = { selectedTeam = team.id }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(team.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                selectedTeam?.let { onConfirm(it) }
            }) {
                Text("확인")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    reservation: Meeting,
    teamId: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("확정하시겠습니까?") },
        text = {
            Column {
                Text("팀: ${reservation.teamName}")
                Text("날짜: ${reservation.date}")
                Text("선택한 팀: $teamId")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("확정")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun ChatDialog(
    meeting: Meeting,
    onDismiss: () -> Unit,
    onStartChat: (String, List<String>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("채팅 시작") },
        text = {
            Column {
                Text("팀 이름: ${meeting.teamName}")
                Text("날짜: ${meeting.date}")
                Text("멤버: ${meeting.members.joinToString(", ")}")
            }
        },
        confirmButton = {
            Button(onClick = {
                onStartChat(meeting.teamName, meeting.members)
            }) {
                Text("채팅 시작")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

private fun initializeSendbird(context: Context, userId: String, onSuccess: () -> Unit) {
    SendbirdChat.init(context, "1EB57A17-6FCF-4781-9828-BC027F97C8EA")
    SendbirdChat.connect(userId) { user, e ->
        if (e != null) {
            Toast.makeText(context, "Sendbird 연결 실패", Toast.LENGTH_SHORT).show()
            return@connect
        }
        onSuccess()
    }
}

private fun createChannel(selectedUsers: List<String>, context: Context) {
    val currentUser = SendbirdChat.currentUser
    if (currentUser == null) {
        Toast.makeText(context, "Sendbird 연결 실패", Toast.LENGTH_SHORT).show()
        return
    }

    val params = GroupChannelCreateParams().apply {
        userIds = selectedUsers + listOf(currentUser.userId)
        isDistinct = true
    }

    GroupChannel.createChannel(params) { channel, e ->
        if (channel != null) {
            val intent = ChannelActivity.newIntent(context, channel.url)
            context.startActivity(intent)
        } else {
            e?.printStackTrace()
            Toast.makeText(context, "채널 생성 실패", Toast.LENGTH_SHORT).show()
        }
    }
}
