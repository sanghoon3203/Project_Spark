package com.example.spark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Project_Spark.ui.theme.ProjectSparkTheme

data class Profile(val department: String, val age: String)

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
fun MeetingWaitingRoomScreen() {
    val femaleProfiles = listOf(
        Profile("항공과", "만 21세"),
        Profile("유교과", "만 22세"),
        Profile("영문과", "만 20세")
    )

    val maleProfiles = listOf(
        Profile("경영학과", "만 21세"),
        Profile("컴공과", "만 22세"),
        Profile("인지융과", "만 20세")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            FilterButtons()
            Spacer(modifier = Modifier.height(16.dp))
            GroupSection(groupName = "여자 그룹")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileList(profiles = femaleProfiles)
            Spacer(modifier = Modifier.height(16.dp))
            GroupSection(groupName = "남자 그룹")
            Spacer(modifier = Modifier.height(8.dp))
            ProfileList(profiles = maleProfiles)
            Spacer(modifier = Modifier.height(16.dp))
            CreateTeamButton()
        }
        BottomNavigationBar(modifier = Modifier.align(Alignment.BottomCenter))
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
fun CreateTeamButton() {
    Button(
        onClick = { /* TODO: 팀 생성하기 로직 */ },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("팀 생성하기", color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = Color.White,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "홈") },
            selected = false,
            onClick = { /* TODO: 홈 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "검색") },
            selected = false,
            onClick = { /* TODO: 검색 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "추가") },
            selected = false,
            onClick = { /* TODO: 추가 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "알림") },
            selected = false,
            onClick = { /* TODO: 알림 */ }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "프로필") },
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
