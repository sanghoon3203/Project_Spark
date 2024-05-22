package com.example.Project_Spark.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.Project_Spark.FriendsActivity
import com.example.Project_Spark.R

@Composable
fun BottomNavigationBar() {
    val context = LocalContext.current

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White
    ) {
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                painter = painterResource(id = R.drawable.home), // 홈 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(30.dp) // Icon 크기 설정
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                painter = painterResource(id = R.drawable.search), // 검색 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(30.dp) // Icon 크기 설정
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                painter = painterResource(id = R.drawable.create), // 추가 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(50.dp) // Icon 크기 설정
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {
            // FriendsActivity로 이동
            val intent = Intent(context, FriendsActivity::class.java)
            context.startActivity(intent)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.chat), // 채팅 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(40.dp) // Icon 크기 설정
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                painter = painterResource(id = R.drawable.defaultprofile), // 프로필 아이콘 리소스
                contentDescription = null,
                modifier = Modifier.size(40.dp) // Icon 크기 설정
            )
        }
    }
}
