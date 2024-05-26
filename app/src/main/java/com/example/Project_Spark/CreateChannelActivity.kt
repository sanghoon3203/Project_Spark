package com.example.Project_Spark

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.android.user.User
import com.sendbird.uikit.activities.ChannelActivity

class CreateChannelActivity : ComponentActivity() {

    private lateinit var selectedUsers: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedUsers = mutableListOf()
        setContent {
            CreateChannelScreen(selectedUsers)
        }
    }
}

@Composable
fun CreateChannelScreen(selectedUsers: MutableList<String>) {
    val context = LocalContext.current

    var friends by remember { mutableStateOf(listOf("friend1", "friend2", "friend3")) } // 실제로는 API를 통해 친구 목록을 불러와야 함

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        friends.forEach { friend ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = selectedUsers.contains(friend),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedUsers.add(friend)
                        } else {
                            selectedUsers.remove(friend)
                        }
                    }
                )
                Text(friend, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Button(onClick = {
            createChannel(selectedUsers, context)
        }) {
            Text("채팅방 생성")
        }
    }
}

private fun createChannel(selectedUsers: List<String>, context: android.content.Context) {
    val currentUser = SendbirdChat.currentUser
    val params = GroupChannelCreateParams().apply {
        userIds = selectedUsers + listOfNotNull(currentUser?.userId)
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
