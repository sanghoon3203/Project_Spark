package com.example.Project_Spark

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.random.Random
import com.example.Project_Spark.ui.components.BottomNavigationBar


class HomeActivity_friendsmatching : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                HomeScreen()
            }
        }
    }
}

// Firebase Firestore 데이터 모델
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val major: String = "",
    val imageUrl: String = ""
)

// HomeScreen 구성
@Composable
fun HomeScreen() {
    // LocalContext를 사용하여 현재 context를 가져옵니다.
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var friendsRecommendList by remember { mutableStateOf(listOf<UserProfile>()) }

    LaunchedEffect(currentUser) {
        val allUserUids = firestore.collection("users").get().await().documents.map { it.id }
        val friendsList = firestore.collection("users").document(currentUser!!.uid).get().await()
            .get("friends") as? List<String> ?: emptyList()
        val filteredUids = allUserUids.filter { it !in friendsList }
        val shuffledUids = filteredUids.shuffled()

        val profiles = shuffledUids.map { uid ->
            val document = firestore.collection("profiles").document(uid).get().await()
            UserProfile(
                uid = uid,
                name = document.getString("name") ?: "",
                major = document.getString("major") ?: "",
                imageUrl = document.getString("imageUrl") ?: ""
            )
        }
        friendsRecommendList = profiles
    }

    Scaffold(
        topBar = { TopBar(
            onFriendClick = {

            },
            onMeetingClick = { // 친구 탭 클릭 시 수행할 작업
                // HomeActivity_friendsmathcing로 이동
                val intent = Intent(context, HomeActivity_meeting::class.java)
                context.startActivity(intent)
            },
            onBellClick = {
                // HomeActivity_friendsmathcing로 이동
                val intent = Intent(context, HomeActivity_meeting::class.java)
                context.startActivity(intent)

            },
            onFilterClick = {
                // HomeActivity_friendsmathcing로 이동
                val intent = Intent(context, HomeActivity_meeting::class.java)
                context.startActivity(intent)

            })

             },
        bottomBar = { BottomNavigationBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                FriendsBanner()
                FriendsRecommendList(friendsRecommendList)
            }
        }
    )



}

@Composable
fun FriendsRecommendList(friendsRecommendList: List<UserProfile>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(friendsRecommendList.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pair.forEach { profile ->
                    FriendCard(profile)
                }
            }
        }
    }
}

@Composable
fun FriendCard(profile: UserProfile) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(170.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF5D4037)) // 배경 색상을 변경합니다.
            .clickable { expanded = !expanded }
            .shadow(10.dp, RoundedCornerShape(20.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(profile.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = profile.major,
                textAlign = TextAlign.Center
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically) + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically) + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { expanded = false }
            ) {
                Image(
                    painter = rememberImagePainter(profile.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        }
    }
}




@Composable
fun TopBar(
    onFriendClick: () -> Unit,
    onMeetingClick: () -> Unit,
    onBellClick: () -> Unit,
    onFilterClick:()-> Unit
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
            color = Color.Black ,

        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "미팅",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            color = Color.Gray,
            modifier = Modifier.clickable(onClick = onMeetingClick)

        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.filter), // 아이콘 리소스
            contentDescription = null,
            modifier = Modifier
                .size(24.dp) // 아이콘 크기를 지정합니다.
                .clickable(onClick = onFilterClick)

        )
        Icon(
            painter = painterResource(id = R.drawable.bell), // 아이콘 리소스
            contentDescription = null,
            modifier = Modifier
                .size(24.dp) // 아이콘 크기를 지정합니다.
                .clickable(onClick = onBellClick) // 클릭 이벤트 추가

        )
    }
}

@Composable
fun FriendsBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.friendbanner), // 이미지 리소스
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(), // 이미지를 가로 전체로 채우기
            contentScale = ContentScale.FillWidth // 가로 비율을 맞추기 위해 이미지 스케일 조정

        )
    }
}



// 프리뷰
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
