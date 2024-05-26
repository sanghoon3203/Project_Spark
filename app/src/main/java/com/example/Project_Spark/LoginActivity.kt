package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            ProjectSparkTheme {
                LoginScreen(auth, db)
            }
        }
    }
}

@Composable
fun LoginScreen(auth: FirebaseAuth, db: FirebaseFirestore) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signIn(auth, email, password, db, context)
                } else {
                    Toast.makeText(context, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("회원가입")
        }
    }
}

private fun signIn(auth: FirebaseAuth, email: String, password: String, db: FirebaseFirestore, context: android.content.Context) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("LoginActivity", "signInWithEmail:success")
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    // Sendbird 초기화 및 연결
                    initializeSendbird(uid, db, context)
                }
            } else {
                Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun initializeSendbird(userId: String, db: FirebaseFirestore, context: android.content.Context) {
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
                override fun onMigrationStarted() {
                    // DB migration has started.
                }

                override fun onInitFailed(e: SendbirdException) {
                    // If DB migration fails, this method is called.
                    Log.e("Sendbird", "Initialization failed: ${e.message}")
                    Toast.makeText(context, "Sendbird 초기화 실패.", Toast.LENGTH_SHORT).show()
                }

                override fun onInitSucceed() {
                    // If DB migration is successful, this method is called and you can proceed to the next step.
                    connectToSendbird(userId, db, context)
                }
            }
        }
    }, context)
}

private fun connectToSendbird(userId: String, db: FirebaseFirestore, context: android.content.Context) {
    SendbirdChat.connect(userId) { user, e ->
        if (user == null || e != null) {
            e?.printStackTrace()
            Toast.makeText(context, "Sendbird 연결 실패", Toast.LENGTH_SHORT).show()
            return@connect
        }
        // 연결이 성공하면 사용자 프로필 확인
        checkUserProfile(userId, db, context)
    }
}

private fun checkUserProfile(uid: String, db: FirebaseFirestore, context: android.content.Context) {
    val docRef = db.collection("profiles").document(uid)
    docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                // 프로필이 존재하면 HomeActivity_meeting으로 이동
                val intent = Intent(context, HomeActivity_meeting::class.java)
                context.startActivity(intent)
            } else {
                // 프로필이 존재하지 않으면 ProfileActivity로 이동
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            }
        }
        .addOnFailureListener { exception ->
            Log.w("LoginActivity", "Error getting documents: ", exception)
            Toast.makeText(context, "프로필 확인 실패.", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProjectSparkTheme {
        LoginScreen(auth = FirebaseAuth.getInstance(), db = FirebaseFirestore.getInstance())
    }
}
