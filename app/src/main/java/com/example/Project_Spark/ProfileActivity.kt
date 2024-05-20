package com.example.Project_Spark

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Project_Spark.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var storageRef: StorageReference
    private lateinit var user: FirebaseUser
    private var imageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 71
        private const val TAG = "ProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase User와 Storage Reference 초기화
        user = FirebaseAuth.getInstance().currentUser!!
        storageRef = FirebaseStorage.getInstance().reference.child("UserProfile")

        // 프로필 존재 여부를 확인
        checkProfileExists()

        // View Binding 설정
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이미지 선택 버튼 클릭 리스너 설정
        binding.btnChooseImage.setOnClickListener {
            chooseImage()
        }

        // 프로필 저장 버튼 클릭 리스너 설정
        binding.btnSaveProfile.setOnClickListener {
            uploadImage()
        }
    }

    // 프로필이 존재하는지 확인하는 함수
    private fun checkProfileExists() {
        val profileRef = storageRef.child("${user.uid}")

        // 프로필 이미지의 다운로드 URL을 확인
        profileRef.downloadUrl.addOnSuccessListener {
            // 프로필 이미지가 존재하면 HomeActivity_meeting로 이동
            val intent = Intent(this, HomeActivity_meeting::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            // 프로필 이미지가 없으면 ProfileActivity에 머무름
            Log.d(TAG, "Profile image does not exist, stay in ProfileActivity.")
        }
    }

    // 이미지 선택을 시작하는 함수
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // 이미지 선택 결과를 처리하는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                imageUri = data.data
                // 선택한 이미지를 Picasso 라이브러리를 사용하여 ImageView에 표시
                Picasso.get().load(imageUri).into(binding.imgProfile)
            }
        }
    }

    // 이미지를 업로드하는 함수
    private fun uploadImage() {
        if (imageUri != null) {
            val ref = storageRef.child("${user.uid}")
            ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    // 업로드 성공 후 다운로드 URL을 가져와 프로필 저장
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveProfile(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    // 업로드 실패 시 에러 로그와 토스트 메시지 표시
                    Log.e(TAG, "Image Upload Failed", e)
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    // 프로필 정보를 Firestore에 저장하는 함수
    private fun saveProfile(imageUrl: String) {
        val profile = hashMapOf(
            "name" to binding.etName.text.toString(),
            "email" to user.email,
            "imageUrl" to imageUrl
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("profiles").document(user.uid)
            .set(profile)
            .addOnSuccessListener {
                // 프로필 저장 성공 시 HomeActivity_meeting로 이동
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity_meeting::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // 프로필 저장 실패 시 에러 로그와 토스트 메시지 표시
                Log.e(TAG, "Profile Save Failed", e)
                Toast.makeText(this, "Failed to Save Profile", Toast.LENGTH_SHORT).show()
            }
    }
}
