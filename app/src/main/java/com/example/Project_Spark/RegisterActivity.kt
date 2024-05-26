package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams
import com.sendbird.android.params.UserUpdateParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo

const val SENDBIRD_APP_ID = "1EB57A17-6FCF-4781-9828-BC027F97C8EA"

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 인증 및 Firestore 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        SendbirdUIKit.init(object : SendbirdUIKitAdapter {
            override fun getAppId(): String = SENDBIRD_APP_ID

            override fun getAccessToken(): String = ""

            override fun getUserInfo(): UserInfo {
                val user = auth.currentUser
                return object : UserInfo {
                    override fun getUserId(): String = user?.uid ?: ""

                    override fun getNickname(): String = user?.email ?: ""

                    override fun getProfileUrl(): String = ""
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onMigrationStarted() {
                        Log.d(TAG, "DB migration started")
                    }

                    override fun onInitFailed(e: SendbirdException) {
                        Log.e(TAG, "SendbirdUIKit 초기화 실패", e)
                    }

                    override fun onInitSucceed() {
                        Log.d(TAG, "SendbirdUIKit 초기화 성공")
                    }
                }
            }
        }, this)

        // Compose UI 설정
        setContent {
            MaterialTheme {
                RegisterScreen(
                    onRegister = { email, password -> registerUser(email, password) }
                )
            }
        }
    }

    // 사용자 등록 함수
    private fun registerUser(email: String, password: String) {
        // 이메일 형식 검증
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "유효하지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase로 사용자 등록
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    // 인증 이메일 전송
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Log.d(TAG, "sendEmailVerification:success")
                                // Firestore에 사용자 프로필 저장
                                saveUserProfile(user.uid, email)
                                // 자동 로그인
                                signIn(email, password)
                            } else {
                                Log.e(TAG, "sendEmailVerification:failure", verificationTask.exception)
                                Toast.makeText(this, "인증 이메일 전송 실패.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 사용자 프로필 저장 함수
    private fun saveUserProfile(uid: String, email: String) {
        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "nickname" to email.split("@")[0]
        )
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User profile saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user profile", e)
            }
    }

    // 로그인 함수
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        // Firestore에서 프로필 존재 여부 확인
                        checkUserProfile(user.uid, email)
                    }
                } else {
                    Log.e(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 프로필 존재 여부 확인 함수
    private fun checkUserProfile(uid: String, email: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 프로필이 존재하면 Sendbird 연결
                    connectSendbird(uid, email)
                } else {
                    // 프로필이 존재하지 않으면 ProfileActivity로 이동
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking user profile", e)
                Toast.makeText(this, "프로필 확인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Sendbird 연결 함수
    private fun connectSendbird(userId: String, email: String) {
        SendbirdChat.connect(userId) { user, e ->
            if (e != null) {
                Log.e(TAG, "SendbirdChat connect failure", e)
                Toast.makeText(this, "Sendbird 연결 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                return@connect
            }

            // Sendbird 사용자 정보 업데이트
            val userUpdateParams = UserUpdateParams().apply {
                nickname = email.split("@")[0]
            }
            SendbirdChat.updateCurrentUserInfo(userUpdateParams) { e ->
                if (e != null) {
                    Log.e(TAG, "SendbirdChat update user info failure", e)
                    Toast.makeText(this, "Sendbird 사용자 정보 업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@updateCurrentUserInfo
                }

                Log.d(TAG, "SendbirdChat user info updated")
                // HomeActivity로 이동
                startActivity(Intent(this, HomeActivity_meeting::class.java))
                finish()
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegister: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 이메일 입력 필드
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            // 비밀번호 입력 필드
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            // 회원가입 버튼
            Button(onClick = { onRegister(email, password) }) {
                Text("회원가입")
            }
        }
    }
}
