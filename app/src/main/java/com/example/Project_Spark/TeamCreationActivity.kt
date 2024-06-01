package com.example.Project_Spark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.model.Friend // 수정된 부분
import com.example.Project_Spark.ui.components.BottomNavigationBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                val navController = rememberNavController()
                TeamCreateScreen(navController)
            }
        }
    }
}

@Composable
fun TeamCreateScreen(navController: NavController, viewModel: TeamCreateViewModel = hiltViewModel()) {
    val teamName = remember { mutableStateOf("") }
    val department = remember { mutableStateOf("") }
    val teamDescription = remember { mutableStateOf("") }
    val teamMembers = remember { mutableStateListOf<String>() }
    val showMemberDialog = remember { mutableStateOf(false) }
    val friendsList by viewModel.friendsList.collectAsState()

    if (showMemberDialog.value) {
        MemberSelectionDialog(friendsList, onDismiss = { showMemberDialog.value = false }) { selectedMembers ->
            teamMembers.addAll(selectedMembers)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TeamCreateTopBar(navController = navController)

        TextField(
            value = teamName.value,
            onValueChange = { teamName.value = it },
            label = { Text("팀 이름") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = department.value,
            onValueChange = { department.value = it },
            label = { Text("학과") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = teamDescription.value,
            onValueChange = { teamDescription.value = it },
            label = { Text("팀 소개") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "팀원 추가")
            IconButton(onClick = { viewModel.fetchFriends(); showMemberDialog.value = true }) {
                Icon(painterResource(id = R.drawable.create), contentDescription = "Add")
            }
        }

        teamMembers.forEach { member ->
            Text(text = member)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.createTeam(teamName.value, department.value, teamDescription.value, teamMembers)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "팀 생성")
        }
    }

    BottomNavigationBar()
}
@Composable
fun TeamCreateTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("팀 생성") },
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
fun MemberSelectionDialog(friends: List<Friend>, onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    val selectedMembers = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.background,
            contentColor = contentColorFor(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "팀원 선택", style = MaterialTheme.typography.h6)

                LazyColumn {
                    items(friends) { friend ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedMembers.contains(friend.id),
                                onCheckedChange = {
                                    if (selectedMembers.contains(friend.id)) {
                                        selectedMembers.remove(friend.id)
                                    } else if (selectedMembers.size < 3) {
                                        selectedMembers.add(friend.id)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = friend.name)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "취소")
                    }
                    TextButton(onClick = { onConfirm(selectedMembers); onDismiss() }) {
                        Text(text = "등록")
                    }
                }
            }
        }
    }
}
