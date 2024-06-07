package com.example.Project_Spark

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MeetingListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                MeetingListScreen()
            }
        }
    }
}

@Composable
fun MeetingListScreen() {
    val meetings = listOf(
        "Meeting 1",
        "Meeting 2",
        "Meeting 3"
    )

    var showDialog by remember { mutableStateOf(false) }
    var selectedMeeting by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Meeting List",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(meetings) { meeting ->
                    MeetingItem(meeting) {
                        Log.d("MeetingListScreen", "매칭 버튼 클릭: $meeting")
                        selectedMeeting = meeting
                        showDialog = true
                    }
                }
            }
        }

        if (showDialog) {
            ConfirmMatchDialog(
                onDismiss = {
                    Log.d("MeetingListScreen", "매칭 취소")
                    showDialog = true
                },
                onConfirm = {
                    selectedMeeting?.let {
                        Log.d("MeetingListScreen", "매칭 확정: $it")
                        saveMatchToFirestore(it, context)
                    }
                    showDialog = true
                }
            )
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("미팅 생성")
        }
    }
}

@Composable
fun MeetingItem(meeting: String, onMatchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(meeting, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onMatchClick() }) {
            Text("매칭")
        }
    }
}

@Composable
fun ConfirmMatchDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "매칭 확인") },
        text = { Text("매칭하시겠습니까?") },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("확정")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("취소")
            }
        }
    )
}

fun saveMatchToFirestore(meetingName: String, context: android.content.Context) {
    val db = FirebaseFirestore.getInstance()
    val matchNumber = UUID.randomUUID().toString()
    val data = hashMapOf(
        "meetingName" to meetingName,
        "timestamp" to System.currentTimeMillis()
    )
    db.collection("meeting_fixing")
        .document(matchNumber)
        .set(data)
        .addOnSuccessListener {
            Log.d("MeetingListScreen", "매칭 성공: $matchNumber")
            Toast.makeText(context, "매칭이 성공적으로 저장되었습니다", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Log.e("MeetingListScreen", "매칭 실패", e)
            Toast.makeText(context, "매칭 저장에 실패했습니다", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showBackground = true)
@Composable
fun MeetingListScreenPreview() {
    ProjectSparkTheme {
        MeetingListScreen()
    }
}
