package com.example.projectspark
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        buttonLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    // 원하는 다음 액티비티로 이동하거나 원하는 작업을 수행할 수 있습니다.
                } else {
                    // 로그인 실패
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
