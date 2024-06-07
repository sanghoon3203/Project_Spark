package com.example.Project_Spark

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

const val TAG = "ProfileActivity"

class ProfileActivity : ComponentActivity() {
    private lateinit var storageRef: StorageReference
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase User와 Storage Reference 초기화
        user = FirebaseAuth.getInstance().currentUser!!
        storageRef = FirebaseStorage.getInstance().reference.child("UserProfile")

        // Jetpack Compose를 사용하여 UI 설정
        setContent {
            ProjectSparkTheme {
                ProfileScreen(user, storageRef)
            }
        }
    }
}

@Composable
fun ProfileScreen(user: FirebaseUser, storageRef: StorageReference) {
    // UI 상태 관리
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var isIntrovert by remember { mutableStateOf(true) }
    var major by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    val context = LocalContext.current

    // 이미지 선택 후 결과를 처리하는 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // 이미지 업로드 및 프로필 저장
    fun uploadImageAndSaveProfile() {
        if (imageUri != null) {
            val ref = storageRef.child("${user.uid}")
            ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveProfile(user, name, bio, isIntrovert, major, studentId, uri.toString(), context)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Image Upload Failed", e)
                    Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    // 프로필 화면 UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 프로필 이미지 표시
        if (imageUri != null) {
            Image(
                painter = rememberImagePainter(data = imageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 이미지 선택 버튼
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("프로필사진 선택")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 이름 입력 필드
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default // 기본 키보드 옵션을 사용하여 한글 입력 가능하게 설정
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 자기소개 입력 필드
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("자기소개") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default // 기본 키보드 옵션을 사용하여 한글 입력 가능하게 설정
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 성격 선택 (내향형/외향형)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("성격: ")
            RadioButton(
                selected = isIntrovert,
                onClick = { isIntrovert = true }
            )
            Text("내향형")
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = !isIntrovert,
                onClick = { isIntrovert = false }
            )
            Text("외향형")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 학과 입력 필드
        OutlinedTextField(
            value = major,
            onValueChange = { major = it },
            label = { Text("학과") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default // 기본 키보드 옵션을 사용하여 한글 입력 가능하게 설정
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 학번 입력 필드
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("학번") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 프로필 저장 버튼
        Button(onClick = { uploadImageAndSaveProfile() }) {
            Text("프로필 완성!")
        }
    }
}

// 프로필 정보를 Firestore에 저장하는 함수
fun saveProfile(
    user: FirebaseUser,
    name: String,
    bio: String,
    isIntrovert: Boolean,
    major: String,
    studentId: String,
    imageUrl: String,
    context: Context
) {
    val profile = hashMapOf(
        "name" to name,
        "bio" to bio,
        "isIntrovert" to isIntrovert,
        "major" to major,
        "studentId" to studentId,
        "email" to user.email,
        "imageUrl" to imageUrl
    )

    val db = FirebaseFirestore.getInstance()
    db.collection("profiles").document(user.uid)
        .set(profile)
        .addOnSuccessListener {
            Toast.makeText(context, "Profile Saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, HomeActivity_meeting::class.java)
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Profile Save Failed", e)
            Toast.makeText(context, "Failed to Save Profile", Toast.LENGTH_SHORT).show()
        }
}

// 프리뷰를 위한 컴포저블 함수
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProjectSparkTheme {
        // Dummy user and storage reference for preview purposes
        val dummyUser = FirebaseAuth.getInstance().currentUser!! // Dummy user object
        val dummyStorageRef = FirebaseStorage.getInstance().reference.child("UserProfile") // Dummy storage reference
        ProfileScreen(dummyUser, dummyStorageRef)
    }
}
