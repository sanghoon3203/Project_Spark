package com.example.Project_Spark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전달된 사용자 ID를 가져옴
        val userIdToBlock = intent.getStringExtra("userIdToBlock") ?: ""

        setContent {
            ProjectSparkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserBlockScreen(userIdToBlock)
                }
            }
        }
    }
}

@Composable
fun UserBlockScreen(userIdToBlock: String) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사용자를",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "차단",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = "하시겠습니까?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        blockUser(db, userIdToBlock, context)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "예", color = Color.Black)
            }
            Button(
                onClick = {
                    // 아니요 버튼 클릭 시 FriendsActivity로 이동
                    val intent = Intent(context, FriendsActivity::class.java)
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "아니오", color = Color.Black)
            }
        }
    }
}

suspend fun blockUser(db: FirebaseFirestore, userIdToBlock: String, context: Context) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser == null) {
        Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        return
    }

    val userDocRef = db.collection("users").document(currentUser.uid)

    userDocRef.update("blocked_users", FieldValue.arrayUnion(userIdToBlock))
        .addOnSuccessListener {
            // 차단 성공 시 Toast 메시지 표시 후 FriendsActivity로 이동
            Toast.makeText(context, "사용자를 성공적으로 차단했습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, FriendsActivity::class.java)
            context.startActivity(intent)
        }
        .addOnFailureListener { e ->
            // 차단 실패 시 Toast 메시지 표시
            Toast.makeText(context, "사용자 차단에 실패했습니다. ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showBackground = true)
@Composable
fun BlockPreview() {
    ProjectSparkTheme {
        UserBlockScreen("user_id_to_block")
    }
}
