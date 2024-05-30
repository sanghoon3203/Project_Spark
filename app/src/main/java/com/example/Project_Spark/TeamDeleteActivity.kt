package com.example.Project_Spark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.meetingapp.TeamDeleteViewModel
import com.example.meetingapp.ui.theme.MeetingAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamDeleteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                val navController = rememberNavController()
                TeamDeleteScreen(navController)
            }
        }
    }
}

@Composable
fun TeamDeleteScreen(navController: NavController, viewModel: TeamDeleteViewModel = hiltViewModel()) {
    val teams = viewModel.teams.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "팀 삭제", style = MaterialTheme.typography.h4)

        LazyColumn {
            items(teams) { team ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = team.name)
                    IconButton(onClick = { viewModel.deleteTeam(team.id) }) {
                        Icon(painterResource(id = R.drawable.delete_icon), contentDescription = "Delete")
                    }
                }
            }
        }
    }

    BottomNavigationBar(navController)
}

