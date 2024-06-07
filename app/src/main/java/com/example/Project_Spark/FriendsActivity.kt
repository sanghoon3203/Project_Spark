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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.viewmodel.FriendsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.Project_Spark.ui.components.BottomNavigationBar

class FriendsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 로그인된 사용자의 ID 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // userId가 유효하지 않은 경우 처리
        if (userId.isEmpty()) {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Jetpack Compose를 사용하여 UI 설정
        setContent {
            ProjectSparkTheme {
                FriendsScreen(userId)
            }
        }
    }
}

@Composable
fun FriendsScreen(userId: String, viewModel: FriendsViewModel = viewModel()) {
    val friendsList by viewModel.friendsList.collectAsState() // 친구 목록 상태 수집
    val profileImageUri by viewModel.profileImageUri.collectAsState() // 프로필 이미지 URI 상태 수집
    val context = LocalContext.current

    // 사용자 ID가 변경될 때마다 프로필 이미지와 친구 목록 로드
    LaunchedEffect(userId) {
        viewModel.loadProfileImage(userId)
        viewModel.loadFriends(userId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 헤더
        TopBar(navController = rememberNavController())
        // 친구 추가 버튼
        AddFriendButton(userId, viewModel)
        // 친구 목록
        FriendList(friendsList, userId, viewModel, Modifier.weight(1f))
        // 바텀 네비게이션 바
        BottomNavigationBar()
    }
}

@Composable
fun FriendList(friendsList: List<Friend>, userId: String, viewModel: FriendsViewModel, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        item {
            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "스친 ",
                fontSize = 12.sp,
                lineHeight = 21.sp,
                fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 친구 목록을 반복하여 각 친구 항목을 표시
        items(friendsList) { friends ->
            FriendItem(friend = friends, onDelete = {
                viewModel.deleteFriend(userId, friends.id)
            })
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun AddFriendButton(userId: String, viewModel: FriendsViewModel) {
    var newFriendEmail by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = newFriendEmail,
            onValueChange = { newFriendEmail = it },
            label = { Text("친구 이메일 입력",fontFamily = FontFamily(Font(R.font.applesdgothicneobold))) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.addFriendByEmail(userId, newFriendEmail,) { success ->
                if (success) {
                    Toast.makeText(context, "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "친구 요청을 보내는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        },                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7DD8C6)) // 여기에서 색상을 지정합니다.
        ) {
            Text("친구 추가",fontFamily = FontFamily(Font(R.font.applesdgothicneobold)))
        }
    }
}





@Composable
fun FriendItem(friend: Friend, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지 표시
        Image(
            painter = rememberImagePainter(data = friend.profileImageUrl ?: R.drawable.user_circle),
            contentDescription = "Profile Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 친구 이름 표시
        Text(
            friend.name,
            fontSize =18.sp,
            lineHeight = 21.sp,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 채팅 아이콘

        Spacer(modifier = Modifier.width(8.dp))
        // 차단 아이콘
        Image(
            painter = painterResource(id = R.drawable.close_ring),
            contentDescription = "Block",
            contentScale = ContentScale.None,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 삭제 버튼
        Button(onClick = onDelete,                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7DD8C6)) // 여기에서 색상을 지정합니다.
        ) {
            Text("친구삭제")
        }
    }
}



// FriendsScreen 미리보기용 프리뷰 함수
@Preview(showBackground = true)
@Composable
fun FriendsScreenPreview() {
    ProjectSparkTheme {
        FriendsScreen("Fh6dHI8xCQZXdKqw9G4WRjSADeX2")
    }
}
@Composable
fun TopBar(navController: NavController) {
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))
    TopAppBar(
        title = { Text("친구목록", fontFamily = fontFamily) },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("HomeActivity_meeting") }) {
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

