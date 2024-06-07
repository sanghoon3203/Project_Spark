package com.example.Project_Spark

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.meetingapp.MeetingCreateViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Calendar

@AndroidEntryPoint
class MeetingReservationActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                val navController = rememberNavController()
                MeetingCreateScreen(navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingCreateScreen(navController: NavController, viewModel: MeetingCreateViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val selectedTeam = remember { mutableStateOf<String?>(null) }
    val meetingDate = remember { mutableStateOf(LocalDate.now()) }
    val showDialog = remember { mutableStateOf(false) }
    val teams = viewModel.teams.collectAsState().value
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("미팅을 생성하시겠습니까?", fontFamily = fontFamily) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createMeeting(selectedTeam.value, meetingDate.value)
                    Toast.makeText(context, "미팅이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                    showDialog.value = false
                }) {
                    Text("생성", fontFamily = fontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("취소", fontFamily = fontFamily)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            topBar = {
                MeetingCreationTopBar(navController = navController)
            },
            bottomBar = {
                BottomNavigationBar()
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "팀 선택", style = MaterialTheme.typography.h6, fontFamily = fontFamily)
                    }

                    items(teams) { team ->
                        MyTeamItem(team = team, selectedTeam = selectedTeam.value, onSelect = { selectedTeam.value = it })
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "예약 날짜", style = MaterialTheme.typography.h6, fontFamily = fontFamily)

                        DatePicker(
                            selectedDate = meetingDate.value,
                            onDateChange = { meetingDate.value = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                showDialog.value = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7DD8C6))
                        ) {
                            Text(text = "미팅 생성", fontFamily = fontFamily)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        )
    }
}

@Composable
fun MyTeamItem(team: Team, selectedTeam: String?, onSelect: (String) -> Unit) {
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))
    val isSelected = selectedTeam == team.id

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onSelect(team.id) },
        backgroundColor = if (isSelected) Color.LightGray else Color.White,
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onSelect(team.id) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = team.name, fontFamily = fontFamily, fontSize = 16.sp)
        }
    }
}

@Composable
fun MeetingCreationTopBar(navController: NavController, modifier: Modifier = Modifier) {
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))
    val context = LocalContext.current
    TopAppBar(
        title = { Text("미팅 생성", fontFamily = fontFamily) },
        navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, HomeActivity_meeting::class.java)
                context.startActivity(intent)
            }){
                Icon(painterResource(id = R.drawable.expand_left), contentDescription = "Back")
            }
        },
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF7DD8C6),
        actions = {},
        elevation = 8.dp
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(selectedDate: LocalDate, onDateChange: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        onDateChange(LocalDate.of(selectedYear, selectedMonth + 1, selectedDay))
    }, year, month, day)

    Button(
        onClick = { datePickerDialog.show() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(text = selectedDate.toString(), fontFamily = FontFamily(Font(R.font.applesdgothicneobold)), fontSize = 18.sp)
    }
}

data class MyTeam(val id: String, val name: String)
