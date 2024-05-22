package com.example.Project_Spark

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import coil.compose.rememberImagePainter
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.viewmodel.FriendsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

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
        Header()
        // 친구 목록
        FriendList(friendsList, userId, viewModel)
        // 바텀 네비게이션 바
        BottomNavigationBar()
    }
}

@Composable
fun BottomNavIconButton(iconResId: Int, onClick: () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick) {
        androidx.compose.material3.Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron_left),
            contentDescription = "Back",
            contentScale = ContentScale.None,
            modifier = Modifier.size(50.dp)
        )

        Text(
            "친구 목록",
            fontSize = 24.sp,
            lineHeight = 45.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FriendList(friendsList: List<Friend>, userId: String, viewModel: FriendsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .background(Color.LightGray)
    ) {
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "친구 목록",
            fontSize = 24.sp,
            lineHeight = 21.sp,
            fontFamily = FontFamily(Font(R.font.inter)),
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 친구 목록을 반복하여 각 친구 항목을 표시
        for (friend in friendsList) {
            FriendItem(friend = friend, onDelete = {
                viewModel.deleteFriend(userId, friend.id)
            })
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
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
            painter = rememberImagePainter(data = friend.profileImageUrl ?: R.drawable.defaultprofile),
            contentDescription = "Profile Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 친구 이름 표시
        Text(
            friend.name,
            fontSize = 20.sp,
            lineHeight = 21.sp,
            fontFamily = FontFamily(Font(R.font.inter)),
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 채팅 아이콘
        Image(
            painter = painterResource(id = R.drawable.chat),
            contentDescription = "Chat",
            contentScale = ContentScale.None,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 차단 아이콘
        Image(
            painter = painterResource(id = R.drawable.block),
            contentDescription = "Block",
            contentScale = ContentScale.None,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 삭제 버튼
        Button(onClick = onDelete) {
            Text("Delete")
        }
    }
}

// FriendsScreen 미리보기용 프리뷰 함수
@Preview(showBackground = true)
@Composable
fun FriendsScreenPreview() {
    ProjectSparkTheme {
        FriendsScreen("dummy_user_id")
    }
}