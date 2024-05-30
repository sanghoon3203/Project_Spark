package com.example.Project_Spark

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.meetingapp.MeetingCreateViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Calendar

@AndroidEntryPoint
class MeetingReservationActivity : ComponentActivity() {
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

@Composable
fun MeetingCreateScreen(navController: NavController, viewModel: MeetingCreateViewModel = hiltViewModel()) {
    val selectedTeam = remember { mutableStateOf<String?>(null) }
    val meetingDate = remember { mutableStateOf(LocalDate.now()) }
    val teams = viewModel.teams.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MeetingCreateTopBar(navController = navController)

        Text(text = "팀 선택", style = MaterialTheme.typography.h6)

        LazyColumn {
            items(teams) { team ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTeam.value == team.id,
                        onClick = { selectedTeam.value = team.id }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = team.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "예약 날짜", style = MaterialTheme.typography.h6)
        DatePicker(
            selectedDate = meetingDate.value,
            onDateChange = { meetingDate.value = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.createMeeting(selectedTeam.value, meetingDate.value)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "미팅 생성")
        }
    }

    BottomNavigationBar(navController)
}

@Composable
fun MeetingCreateTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("미팅 생성") },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("home_meeting") }) {
                Icon(painterResource(id = R.drawable.expand_left), contentDescription = "Back")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        actions = {},
        elevation = 8.dp
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.primary
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = false,
            onClick = { navController.navigate("home_meeting") }
        )
        // 다른 바텀 네비게이션 아이템 추가
    }
}

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

    Button(onClick = { datePickerDialog.show() }) {
        Text(text = selectedDate.toString())
    }
}
