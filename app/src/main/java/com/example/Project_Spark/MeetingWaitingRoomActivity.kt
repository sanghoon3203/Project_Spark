package com.example.Project_Spark

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.model.Profile
import com.example.Project_Spark.model.Team
import com.example.Project_Spark.viewmodel.FriendsViewModel
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import java.util.*

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
fun MeetingWaitingRoomScreen(viewModel: FriendsViewModel = viewModel()) {
    val context = LocalContext.current
    var showFriendsList by remember { mutableStateOf(false) }
    var selectedFriends by remember { mutableStateOf(emptyList<Friend>()) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 필터 버튼
        FilterButtons()
        Spacer(modifier = Modifier.height(16.dp))
        // 여자 그룹 섹션
        GroupSection(groupName = "여자 그룹")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileList(profiles = listOf())
        Spacer(modifier = Modifier.height(16.dp))
        // 남자 그룹 섹션
        GroupSection(groupName = "남자 그룹")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileList(profiles = listOf())
        Spacer(modifier = Modifier.height(16.dp))
        // 내 팀으로 미팅 참여하기 버튼
        Button(
            onClick = { showFriendsList = true },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("내 친구로 미팅참여하기", color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 하단 네비게이션 바
        BottomNavigationBar(modifier = Modifier.align(Alignment.CenterHorizontally))
    }

    if (showFriendsList) {
        FriendSelectionDialog(
            friendsList = viewModel.friendsList.collectAsState().value,
            onDismiss = { showFriendsList = false },
            onConfirm = { selectedFriendsList ->
                selectedFriends = selectedFriendsList
                showFriendsList = false

                // 팀 생성 로직
                val team = Team(
                    id = UUID.randomUUID().toString(),
                    name = "My Team",
                    members = selectedFriends.map { it.id }
                )
                viewModel.createTeam(team) {
                    if (it) {
                        Toast.makeText(context, "팀이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "팀 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
fun FilterButtons() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterButton(text = "3:3")
        FilterButton(text = "컴공")
        FilterButton(text = "다음주")
    }
}

@Composable
fun FilterButton(text: String) {
    Button(
        onClick = { /* TODO: 필터 적용 */ },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onSecondary)
    }
}

@Composable
fun GroupSection(groupName: String) {
    Text(
        text = groupName,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        modifier = Modifier.padding(start = 8.dp)
    )
}

@Composable
fun ProfileList(profiles: List<Profile>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        items(profiles) { profile ->
            ProfileCard(department = profile.department, age = profile.age)
        }
    }
}

@Composable
fun ProfileCard(department: String, age: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(4.dp)
            .size(width = 120.dp, height = 160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.White, shape = CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = department, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            Text(text = age, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = Color.White,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Home, contentDescription = "홈") },
            selected = false,
            onClick = { /* TODO: 홈 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Search, contentDescription = "검색") },
            selected = false,
            onClick = { /* TODO: 검색 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "추가") },
            selected = false,
            onClick = { /* TODO: 추가 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Notifications, contentDescription = "알림") },
            selected = false,
            onClick = { /* TODO: 알림 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Person, contentDescription = "프로필") },
            selected = false,
            onClick = { /* TODO: 프로필 */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MeetingWaitingRoomScreenPreview() {
    ProjectSparkTheme {
        MeetingWaitingRoomScreen()
    }
}
