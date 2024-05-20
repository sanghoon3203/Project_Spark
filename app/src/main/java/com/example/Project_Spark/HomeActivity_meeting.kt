package com.example.Project_Spark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import android.content.Intent


class HomeActivity_meeting : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                Homescreen()
            }
        }
    }
}

@Composable
fun Homescreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 탭
        TopTabs()
        // 날짜와 일정 표시
        DateAndSchedule()
        // 참여 및 친구 찾기 버튼
        ActionButtons()
        // 배너
        Banner()
        // 미팅 정보 카드
        MeetingInfo()
        Spacer(modifier = Modifier.weight(1f))
        // 하단 네비게이션 바
        BottomNavigationBar()
    }
}

@Composable
fun TopTabs() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text("친구", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text("미팅", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.bell), // 아이콘 리소스
            contentDescription = null


        )
    }
}

@Composable
fun DateAndSchedule() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("01 Jan 2022", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun ActionButtons() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = { /* Do something */ },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("내 팀으로 미팅 참여 하기", fontSize = 24.sp)
        }
        Button(
            onClick = { /* Do something */ },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("얼른 새로운 친구 만나기", color = Color.Black, fontSize = 24.sp)
        }
    }
}

@Composable
fun Banner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                "인원 수가 많은건 좀 부담인가요?",
                fontSize = 19.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                "새로운 친구를 찾아봐요!!",
                fontSize = 19.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.newjeans),
            contentDescription = "logo",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(top = 8.dp)
                .width(36.dp)
                .height(56.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painter = painterResource(id = R.drawable.newjeans),
            contentDescription = "logo",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(top = 8.dp)
                .width(36.dp)
                .height(56.dp)
        )
    }
}

@Composable
fun MeetingInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("미팅이 시작됐어요!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        MeetingCard()
        Spacer(modifier = Modifier.height(8.dp))
        MeetingCard()
    }
}

@Composable
fun MeetingCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("남", modifier = Modifier.weight(1f))
        Text("간호학과", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("여", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Spacer(modifier = Modifier.width(8.dp))
        Text(">", modifier = Modifier.weight(1f), textAlign = TextAlign.End , fontSize = 30.sp)
    }
}

@Composable

fun BottomNavigationBar() {
    val context = LocalContext.current


    androidx.compose.material3.BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White
    ) {
        androidx.compose.material3.IconButton(onClick = { /* Do something */ }) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.home), // 홈 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.material3.IconButton(onClick = { /* Do something */ }) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.search), // 검색 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.material3.IconButton(onClick = { /* Do something */ }) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.create), // 추가 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.material3.IconButton(onClick = {
            // FriendsActivity로 이동
            val intent = Intent(context, FriendsActivity::class.java)
            context.startActivity(intent) },
            modifier = Modifier.size(30.dp)
        ) {// IconButton 크기 설정 {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.chat), // 채팅 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.material3.IconButton(onClick = { /* Do something */ }) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.defaultprofile), // 프로필 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Homepreview() {
    ProjectSparkTheme {
        Homescreen()
    }
}
