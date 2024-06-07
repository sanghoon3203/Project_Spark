package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.ui.components.BottomNavigationBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeActivity_meeting : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                Homescreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Homescreen() {
    // LocalContext를 사용하여 현재 context를 가져옵니다.
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 탭
        TopTabs(
            onFriendClick = {
                // 친구 탭 클릭 시 수행할 작업
                // HomeActivity_friendsmathcing로 이동
                val intent = Intent(context, HomeActivity_friendsmatching::class.java)
                context.startActivity(intent)
            },
            onMeetingClick = {
                // 미팅 탭 클릭 시 수행할 작업
                println("미팅 clicked")
            },
            onBellClick = {
                // 벨 아이콘 클릭 시 수행할 작업
                println("Bell icon clicked")
            }
        )
        // 날짜와 일정 표시
        DateAndSchedule()
        // 참여 및 친구 찾기 버튼
        ActionButtons()
        // 배너
        Banner()
        // 미팅 정보 카드
        MeetingInfo()
        Spacer(modifier = Modifier.weight(1f))
        MeetingMK()
        // 하단 네비게이션 바
        BottomNavigationBar()
    }
}
@Composable
fun TopTabs(
    onFriendClick: () -> Unit,
    onMeetingClick: () -> Unit,
    onBellClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            "친구",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraLight,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            color = Color.Gray,
            modifier = Modifier.clickable(onClick = onFriendClick)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "미팅",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            modifier = Modifier.clickable(onClick = onMeetingClick)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.bell), // 아이콘 리소스
            contentDescription = null,
            modifier = Modifier
                .size(24.dp) // 아이콘 크기를 지정합니다.
                .clickable(onClick = onBellClick)
        )
    }
}

@Composable
fun xmlImage(drawableResId: Int, modifier: Modifier = Modifier) {
    val painter = painterResource(id = drawableResId)

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(100.dp)
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateAndSchedule() {
    val currentDate = LocalDate.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy MMM dd"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(formattedDate,
            fontSize = 14.sp,
            color = Color.Gray,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
        )
    }
}

@Composable
fun ActionButtons() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {   val intent = Intent(context, MeetingWaitingRoomActivity::class.java)
                context.startActivity(intent) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                "내 팀으로 미팅 참여 하기",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            )
        }
        Button(
            onClick = {
                val intent = Intent(context, HomeActivity_friendsmatching::class.java)
                context.startActivity(intent)
                },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(
                "얼른 새로운 친구 만나기",
                color = Color.Black,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            )
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
fun MeetingInfo() {}

@Composable
fun MeetingMK() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) // 전체 Row의 패딩을 추가합니다
    ) {
        xmlImage(
            drawableResId = R.drawable.meetingbutton,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .clickable {
                    val intent = Intent(context, MeetingReservationActivity::class.java)
                    context.startActivity(intent)
                }
        )
        xmlImage(
            drawableResId = R.drawable.teambutton,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .clickable {
                    val intent = Intent(context, TeamCreationActivity::class.java)
                    context.startActivity(intent)
                }
        )
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


@Preview(showBackground = true)
@Composable
fun Homepreview() {
    ProjectSparkTheme {
        Homescreen()
    }
}
