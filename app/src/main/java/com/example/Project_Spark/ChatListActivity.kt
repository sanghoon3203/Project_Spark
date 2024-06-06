package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.query.GroupChannelListQuery
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.GroupChannelListQueryParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo
import com.sendbird.uikit.activities.ChannelActivity

class ChatListActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            initializeSendbird(userId)
        } else {
            Toast.makeText(this, "User ID is not available", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeSendbird(userId: String) {
        SendbirdUIKit.init(object : SendbirdUIKitAdapter {
            override fun getAppId(): String {
                return "1EB57A17-6FCF-4781-9828-BC027F97C8EA" // Specify your Sendbird application ID.
            }

            override fun getAccessToken(): String {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        return userId // 로그인한 사용자 ID 사용
                    }

                    override fun getNickname(): String {
                        return "" // Specify your user nickname. Optional.
                    }

                    override fun getProfileUrl(): String {
                        return ""
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onInitSucceed() {
                        // 초기화 성공 후 연결
                        connectToSendbird(userId)
                    }

                    override fun onInitFailed(e: SendbirdException) {
                        // 초기화 실패 처리
                        Log.e("Sendbird", "Initialization failed: ${e.message}")
                        Toast.makeText(this@ChatListActivity, "Sendbird 초기화 실패.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onMigrationStarted() {
                        // 데이터베이스 마이그레이션이 시작되었을 때 처리
                    }
                }
            }
        }, this)
    }

    private fun connectToSendbird(userId: String) {
        SendbirdChat.connect(userId) { user, e ->
            if (user == null || e != null) {
                e?.printStackTrace()
                Toast.makeText(this, "Sendbird 연결 실패", Toast.LENGTH_SHORT).show()
                return@connect
            }
            // 연결 성공 후 채팅 화면 설정
            setContent {
                ChatListScreen()
            }
        }
    }
}
@Composable
fun ChatListScreen() {
    val context = LocalContext.current as AppCompatActivity
    val channels = remember { mutableStateListOf<GroupChannel>() }

    LaunchedEffect(Unit) {
        val params = GroupChannelListQueryParams().apply {
            includeEmpty = true // 원하는 파라미터 설정
            limit = 20 // 원하는 제한 설정
        }
        val query = GroupChannel.createMyGroupChannelListQuery(params)
        query.next { groupChannels, e ->
            if (e != null) {
                Log.e("ChatListScreen", "Error fetching channel list: ${e.message}")
                return@next
            }
            groupChannels?.let {
                channels.addAll(it)

            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, CreateChannelActivity::class.java)
                context.startActivity(intent)
            }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(channels) { channel ->
                ChannelItem(channel = channel, onClick = {
                    val intent = ChannelActivity.newIntent(context, channel.url)
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun ChannelItem(channel: GroupChannel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = channel.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Members: ${channel.memberCount}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
