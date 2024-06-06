package com.example.Project_Spark

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.viewmodel.TeamCreateViewModel
import com.google.firebase.auth.FirebaseAuth
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
    val context = LocalContext.current
    val friendsList by viewModel.friendsList.collectAsState()

    // 현재 로그인된 사용자의 ID 가져오기
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // userId가 유효하지 않은 경우 처리
    if (userId.isEmpty()) {
        Toast.makeText(context, "Failed to get user ID", Toast.LENGTH_SHORT).show()
        return
    }

    LaunchedEffect(userId) {
        viewModel.fetchFriends()
    }

    if (showMemberDialog.value) {
        MemberSelectionDialog(friendsList, onDismiss = { showMemberDialog.value = false }) { selectedMembers ->
            selectedMembers.forEach { member ->
                if (!teamMembers.contains(member)) {
                    teamMembers.add(member)
                }
            }
        }
    }

    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            TextField(
                value = teamName.value,
                onValueChange = { teamName.value = it },
                label = { Text("팀 이름", fontFamily = fontFamily) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily)
            )

            TextField(
                value = department.value,
                onValueChange = { department.value = it },
                label = { Text("학과", fontFamily = fontFamily) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily)
            )

            TextField(
                value = teamDescription.value,
                onValueChange = { teamDescription.value = it },
                label = { Text("팀 소개", fontFamily = fontFamily) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "팀원 추가", fontFamily = fontFamily)
                IconButton(onClick = { showMemberDialog.value = true }) {
                    Icon(painterResource(id = R.drawable.create), contentDescription = "Add")
                }
            }

            teamMembers.forEach { member ->
                Text(text = member, fontFamily = fontFamily)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createTeam(teamName.value, department.value, teamDescription.value, teamMembers)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7DD8C6)) // 여기에서 색상을 지정합니다.


            ) {
                Text(text = "팀 생성", fontFamily = fontFamily)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavigationBar()
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            TeamCreateTopBar(navController = navController)
        }
    }
}

@Composable
fun TeamCreateTopBar(navController: NavController) {
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))
    TopAppBar(
        title = { Text("팀 생성", fontFamily = fontFamily) },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("home_meeting") }) {
                Icon(painterResource(id = R.drawable.expand_left), contentDescription = "Back")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            ,
        backgroundColor = Color(0xFF7DD8C6),
        actions = {},
        elevation = 8.dp
    )
}

@Composable
fun FriendItem(friend: Friend, isSelected: Boolean, onSelect: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelect
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = rememberAsyncImagePainter(model = friend.profileImageUrl ?: R.drawable.user_circle),
            contentDescription = "Profile Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            friend.name,
            fontSize = 20.sp,
            lineHeight = 21.sp,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun MemberSelectionDialog(friends: List<Friend>, onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    val selectedMembers = remember { mutableStateListOf<String>() }
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.background,
            contentColor = contentColorFor(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "팀원 선택", style = MaterialTheme.typography.h6, fontFamily = fontFamily)

                LazyColumn {
                    items(friends) { friend ->
                        FriendItem(
                            friend = friend,
                            isSelected = selectedMembers.contains(friend.id),
                            onSelect = { isSelected ->
                                if (isSelected) {
                                    if (selectedMembers.size < 3) {
                                        selectedMembers.add(friend.id)
                                    }
                                } else {
                                    selectedMembers.remove(friend.id)
                                }
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "취소", fontFamily = fontFamily)
                    }
                    TextButton(onClick = { onConfirm(selectedMembers); onDismiss() }) {
                        Text(text = "등록", fontFamily = fontFamily)
                    }
                }
            }
        }
    }
}
