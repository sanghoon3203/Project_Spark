package com.example.Project_Spark

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.uikit.activities.ChannelActivity
import com.sendbird.android.exception.SendbirdException
import com.example.Project_Spark.model.Friend

class CreateChannelActivity : ComponentActivity() {

    private lateinit var selectedUsers: MutableList<String>
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 로그인된 사용자의 ID 가져오기
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        selectedUsers = mutableListOf()
        setContent {
            CreateChannelScreen(userId, selectedUsers)
        }
    }
}

@Composable
fun CreateChannelScreen(userId: String, selectedUsers: MutableList<String>) {
    val context = LocalContext.current
    var friends by remember { mutableStateOf(listOf<Friend>()) }

    // 친구 목록 불러오기
    LaunchedEffect(userId) {
        loadFriends(userId) { friendList ->
            friends = friendList
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 친구 목록 표시
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(friends) { friend ->
                FriendItem(friend = friend, onDelete = {
                    selectedUsers.remove(friend.id)
                }, onSelect = { isSelected ->
                    if (isSelected) {
                        selectedUsers.add(friend.id)
                    } else {
                        selectedUsers.remove(friend.id)
                    }
                })
            }
        }

        // 채팅방 생성 버튼
        Button(
            onClick = {
                createChannel(selectedUsers, context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("채팅방 생성")
        }
    }
}

private fun loadFriends(userId: String, callback: (List<Friend>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val friendsRef = db.collection("users").document(userId)

    friendsRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val friendsIds = document.get("friends") as? List<String> ?: emptyList()
                val friendList = mutableListOf<Friend>()

                // 친구 ID 목록을 통해 각각의 프로필 데이터를 가져옴
                friendsIds.forEach { friendId ->
                    val profileRef = db.collection("profiles").document(friendId)
                    profileRef.get()
                        .addOnSuccessListener { profileDoc ->
                            if (profileDoc != null) {
                                val name = profileDoc.getString("name") ?: "Unknown"
                                val profileImageUrl = profileDoc.getString("profileImageUrl") ?: ""
                                friendList.add(Friend(friendId, name, profileImageUrl))
                                // 모든 친구 데이터를 가져온 후 콜백 호출
                                if (friendList.size == friendsIds.size) {
                                    callback(friendList)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            callback(emptyList())
                        }
                }
            } else {
                callback(emptyList())
            }
        }
        .addOnFailureListener { exception ->
            callback(emptyList())
        }
}

private fun createChannel(selectedUsers: List<String>, context: android.content.Context) {
    // 이미 연결된 상태이므로 현재 사용자 ID를 가져옵니다.
    val currentUser = SendbirdChat.currentUser
    if (currentUser == null) {
        Toast.makeText(context, "Sendbird 연결 실패", Toast.LENGTH_SHORT).show()
        return
    }

    val params = GroupChannelCreateParams().apply {
        userIds = selectedUsers + listOf(currentUser.userId)
        isDistinct = true
    }

    GroupChannel.createChannel(params) { channel, e ->
        if (channel != null) {
            val intent = ChannelActivity.newIntent(context, channel.url)
            context.startActivity(intent)
        } else {
            e?.printStackTrace()
            Toast.makeText(context, "채널 생성 실패", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun FriendItem(friend: Friend, onDelete: () -> Unit, onSelect: (Boolean) -> Unit) {
    var isSelected by remember { mutableStateOf(false) }

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
            color = androidx.compose.ui.graphics.Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 선택 체크박스
        Checkbox(
            checked = isSelected,
            onCheckedChange = { isChecked ->
                isSelected = isChecked
                onSelect(isChecked)
            }
        )
    }
}
