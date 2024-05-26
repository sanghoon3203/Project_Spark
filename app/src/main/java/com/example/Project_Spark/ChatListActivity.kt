package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.commit
import com.sendbird.uikit.fragments.ChannelListFragment
import com.sendbird.uikit.widgets.StatusFrameView

class ChatListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatListScreen()
        }
    }
}

@Composable
fun ChatListScreen() {
    val context = LocalContext.current as AppCompatActivity

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
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { ctx ->
                StatusFrameView(ctx).apply {
                    id = View.generateViewId()  // Generate a unique ID for the view
                    val fragment = ChannelListFragment.Builder().build()
                    (ctx as AppCompatActivity).supportFragmentManager.commit {
                        replace(this@apply.id, fragment)
                    }
                }
            }
        )
    }
}
