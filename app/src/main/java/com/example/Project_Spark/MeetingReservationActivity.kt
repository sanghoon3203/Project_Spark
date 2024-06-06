package com.example.Project_Spark

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val selectedTeam = remember { mutableStateOf<String?>(null) }
    val meetingDate = remember { mutableStateOf(LocalDate.now()) }
    val teams = viewModel.teams.collectAsState().value
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 56.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "팀 선택", style = MaterialTheme.typography.h6, fontFamily = fontFamily)
            }

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
                    Text(text = team.name, fontFamily = fontFamily, fontSize = 16.sp)
                }
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
                        viewModel.createMeeting(selectedTeam.value, meetingDate.value)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7DD8C6))
                ) {
                    Text(text = "미팅 생성", fontFamily = fontFamily)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            MeetingCreatationTopBar(navController = navController)
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

@Composable
fun MeetingCreatationTopBar(navController: NavController, modifier: Modifier = Modifier) {
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    TopAppBar(
        title = { Text("미팅 생성", fontFamily = fontFamily) },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("home_meeting") }) {
                Icon(painterResource(id = R.drawable.expand_left), contentDescription = "Back")
            }
        },
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF7DD8C6),
        actions = {},
        elevation = 8.dp
    )
}
