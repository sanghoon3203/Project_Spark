package com.example.Project_Spark

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import coil.compose.rememberImagePainter
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class FriendsActivity : ComponentActivity() {

    // Firestore 인스턴스를 위한 변수 선언
    private lateinit var db: FirebaseFirestore

    // 현재 사용자 ID를 위한 변수 선언
    private lateinit var userId: String

    // Firestore 리스너 등록을 위한 변수 선언
    private var friendsListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 로그인된 사용자의 ID 가져오기
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // userId가 유효하지 않은 경우 처리
        if (userId.isEmpty()) {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        // Jetpack Compose를 사용하여 UI 설정
        setContent {
            ProjectSparkTheme {
                FriendsScreen(userId, db)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity 종료 시 리스너 제거
        friendsListener?.remove()
    }

    // Firestore에서 친구 목록을 실시간으로 로드하는 함수
    private fun loadFriends(userId: String, onFriendsLoaded: (List<Friend>) -> Unit) {
        friendsListener = db.collection("users").document(userId).collection("friends")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to load friends.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val friendsList = mutableListOf<Friend>()
                if (snapshots != null) {
                    for (document in snapshots) {
                        val friend = document.toObject(Friend::class.java)
                        friendsList.add(friend)
                    }
                }
                onFriendsLoaded(friendsList)
            }
    }

    // Firestore에 친구를 추가하는 함수
    private fun addFriend(userId: String, friendId: String, friend: Friend, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).collection("friends").document(friendId)
            .set(friend)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Firestore에서 친구를 삭제하는 함수
    private fun deleteFriend(userId: String, friendId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).collection("friends").document(friendId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Firebase Storage에서 프로필 이미지를 가져오는 함수
    private fun loadProfileImage(userId: String, onImageLoaded: (Uri?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("UserProfile/$userId.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onImageLoaded(uri)
        }.addOnFailureListener {
            onImageLoaded(null)
        }
    }

    // 친구 목록 화면을 표시하는 Composable 함수
    @Composable
    fun FriendsScreen(userId: String, db: FirebaseFirestore) {
        // 친구 목록 상태 관리
        var friendsList by remember { mutableStateOf(listOf<Friend>()) }
        // 친구 추가 입력 필드 상태 관리
        var friendName by remember { mutableStateOf("") }
        var friendId by remember { mutableStateOf("") }
        // 프로필 이미지 상태 관리
        var profileImageUri by remember { mutableStateOf<Uri?>(null) }
        // 친구 추가 섹션 표시 여부 상태 관리
        var showAddFriendSection by remember { mutableStateOf(false) }
        // CoroutineScope를 사용하여 비동기 작업 처리
        val scope = rememberCoroutineScope()

        // 컴포넌트가 시작될 때 프로필 이미지와 친구 목록 로드
        LaunchedEffect(Unit) {
            loadProfileImage(userId) { uri ->
                profileImageUri = uri
            }
            loadFriends(userId) { friends ->
                friendsList = friends
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더
            Header()
            // 친구 목록과 추가 기능
            mid()
            //바텀 네비게이션
            BottomNavigationBar()
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
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier.size(50.dp)
            )

            Text(
                "친구 목록          ",
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
    fun mid() {
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
                "스친목록",
                fontSize = 24.sp,
                lineHeight = 21.sp,
                fontFamily = FontFamily(Font(R.font.inter)),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            for (i in 1..10) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.defaultprofile),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "장원영",
                        fontSize = 20.sp,
                        lineHeight = 21.sp,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.chat),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.block),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            }
        }
    }

    // 친구 항목을 표시하는 Composable 함수
    @Composable
    fun FriendItem(friend: Friend, onDelete: () -> Unit) {
        // 친구 항목 레이아웃 설정
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 친구 이름과 ID 표시
            Column {
                Text(text = "Name: ${friend.name}")
                Text(text = "ID: ${friend.id}")
            }
            // 친구 삭제 버튼
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }

    // FriendsScreen의 미리보기를 위한 Composable 함수
    @Preview(showBackground = true)
    @Composable
    fun PreviewFriendsScreen() {
        ProjectSparkTheme {
            FriendsScreenPreviewContent()
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
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.IconButton(onClick = { /* Do something */ }) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.search), // 검색 아이콘 리소스
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.IconButton(onClick = { /* Do something */ }) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.create), // 추가 아이콘 리소스
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.IconButton(onClick = {
                // FriendsActivity로 이동
                val intent = Intent(context, FriendsActivity::class.java)
                context.startActivity(intent) },
                modifier = Modifier.size(48.dp)
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

    // FriendsScreen의 미리보기 콘텐츠를 설정하는 Composable 함수
    @Composable
    fun FriendsScreenPreviewContent() {
        // 친구 추가 입력 필드 상태 관리
        var friendName by remember { mutableStateOf("") }
        var friendId by remember { mutableStateOf("") }
        // 친구 추가 섹션 표시 여부 상태 관리

        Column(modifier = Modifier.fillMaxSize()) {
            Header()
            mid()
            Spacer(modifier = Modifier.weight(1f))
            BottomNavigationBar()
        }
    }
}
