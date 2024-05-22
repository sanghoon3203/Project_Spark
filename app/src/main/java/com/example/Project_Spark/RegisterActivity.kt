package com.example.Project_Spark

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {

    // Firebase 인증 인스턴스 변수 선언
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "RegisterActivity" // 로그 태그 상수
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // Compose 화면 설정
        setContent {
            MaterialTheme {
                // 회원가입 화면을 위한 Composable 함수 호출
                RegisterScreen(
                    onRegister = { email, password -> registerUser(email, password) }
                )
            }
        }
    }

    // 사용자 등록 함수
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Log.d(TAG, "sendEmailVerification:success")
                                Toast.makeText(this, "회원가입 성공! 이메일을 확인해주세요.", Toast.LENGTH_LONG).show()

                                // 이메일 확인 후 자동 로그인
                                signIn(email, password)
                            } else {
                                Log.e(TAG, "sendEmailVerification:failure", verificationTask.exception)
                                Toast.makeText(this, "인증 이메일 전송 실패.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "회원가입 실패.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 사용자 로그인 함수
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    // 로그인 성공 시 MainActivity로 이동하는 코드 추가
                    // val intent = Intent(this, MainActivity::class.java)
                    // startActivity(intent)
                    // finish()
                } else {
                    Log.e(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "로그인 실패.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun RegisterScreen(onRegister: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") } // 이메일 상태 변수
    var password by remember { mutableStateOf("") } // 비밀번호 상태 변수

    // 화면 중앙에 배치하는 Box
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 회원가입 폼을 위한 Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // 항목 간의 간격
        ) {
            // 이메일 입력란
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            // 비밀번호 입력란
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
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
