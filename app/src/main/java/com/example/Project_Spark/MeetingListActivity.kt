package com.example.Project_Spark

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MeetingListActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                val navController = rememberNavController()
                MeetingListScreen(navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingListScreen(navController: NavController, viewModel: MeetingListViewModel = hiltViewModel()) {
    val meetingDate = remember { mutableStateOf(LocalDate.now()) }
    val reservations = viewModel.getReservationsForDate(meetingDate.value)
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))
    val context = LocalContext.current
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<MeetingReservation?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "예약 날짜", style = MaterialTheme.typography.h6, fontFamily = fontFamily)

            DatePicker(
                selectedDate = meetingDate.value,
                onDateChange = { meetingDate.value = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(reservations) { reservation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "팀 이름: ${reservation.teamName}", fontFamily = fontFamily, fontSize = 16.sp)
                            Text(text = "날짜: ${reservation.date}", fontFamily = fontFamily, fontSize = 16.sp)
                            Text(text = "멤버: ${reservation.members.joinToString(", ")}", fontFamily = fontFamily, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    selectedTeam = reservation
                                    showConfirmationDialog = true
                                }
                            ) {
                                Text("매칭")
                            }
                        }
                    }
                }
            }
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text(text = "매칭하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedTeam?.let {
                                viewModel.confirmMatching(it, meetingDate.value, context)
                            }
                            showConfirmationDialog = false
                        }
                    ) {
                        Text("확정")
                    }
                },
                dismissButton = {
                    Button(onClick = { showConfirmationDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavigationBar()
        }
    }
}
