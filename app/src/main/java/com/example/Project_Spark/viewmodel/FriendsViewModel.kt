package com.example.Project_Spark.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project_Spark.model.Friend
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance() // Firestore 인스턴스 생성
    private val storage = FirebaseStorage.getInstance() // Firebase Storage 인스턴스 생성
    private var friendsListener: ListenerRegistration? = null // Firestore 리스너 등록 변수

    // 친구 목록 상태를 관리하기 위한 MutableStateFlow
    private val _friendsList = MutableStateFlow<List<Friend>>(emptyList())
    val friendsList: StateFlow<List<Friend>> = _friendsList

    // 프로필 이미지 URI 상태를 관리하기 위한 MutableStateFlow
    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri

    // Firestore에서 친구 목록을 실시간으로 로드하는 함수
    fun loadFriends(userId: String) {
        // Firestore 컬렉션에서 친구 목록을 실시간으로 가져옴
        friendsListener = db.collection("users").document(userId).collection("friends")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // 에러가 발생한 경우
                    return@addSnapshotListener
                }

                val friendsList = mutableListOf<Friend>()
                if (snapshots != null) {
                    // 스냅샷에서 각 문서를 Friend 객체로 변환하여 목록에 추가
                    for (document in snapshots) {
                        val friend = document.toObject(Friend::class.java)
                        friendsList.add(friend)
                    }
                }
                _friendsList.value = friendsList // 상태 업데이트
            }
    }

    // Firestore에서 친구를 삭제하는 함수
    fun deleteFriend(userId: String, friendId: String) {
        db.collection("users").document(userId).collection("friends").document(friendId)
            .delete()
            .addOnSuccessListener {
                // 친구 삭제 성공 시 처리
            }
            .addOnFailureListener { exception ->
                // 친구 삭제 실패 시 처리
            }
    }

    // Firebase Storage에서 프로필 이미지를 가져오는 함수
    fun loadProfileImage(userId: String) {
        val storageRef = storage.reference.child("UserProfile/$userId.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            _profileImageUri.value = uri // 프로필 이미지 URI 상태 업데이트
        }.addOnFailureListener {
            _profileImageUri.value = null // 프로필 이미지 로드 실패 시 null로 설정
        }
    }

    // ViewModel이 클리어될 때 리스너 제거
    override fun onCleared() {
        super.onCleared()
        friendsListener?.remove()
    }
}
