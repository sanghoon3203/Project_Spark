package com.example.Project_Spark.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project_Spark.model.Friend
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
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
        // Firestore에서 사용자의 친구 UID 리스트를 가져옴
        friendsListener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // 오류 발생 시 로그 출력 (필요에 따라 Logcat 확인)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val friendsUids = snapshot.get("friends") as? List<String> ?: emptyList()
                    val friendsList = mutableListOf<Friend>()

                    // 각 친구의 프로필을 가져와서 업데이트
                    friendsUids.forEach { friendId ->
                        fetchFriendProfile(friendId) { friend ->
                            friend?.let {
                                friendsList.add(it)
                                if (friendsList.size == friendsUids.size) {
                                    _friendsList.value = friendsList // 모든 친구를 가져온 후 목록 업데이트
                                }
                            }
                        }
                    }

                    // 친구 목록이 비어 있을 경우 빈 목록 업데이트
                    if (friendsUids.isEmpty()) {
                        _friendsList.value = emptyList()
                    }
                } else {
                    _friendsList.value = emptyList() // 스냅샷이 null이거나 존재하지 않을 경우 빈 목록 설정
                }
            }
    }

    // 특정 친구의 프로필을 Firestore에서 가져오는 함수
    private fun fetchFriendProfile(uid: String, callback: (Friend?) -> Unit) {
        db.collection("profiles").document(uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.getString("name") ?: "Unknown" // 이름이 없을 경우 "Unknown" 설정
                    val profileImageUrl = snapshot.getString("imgurl")
                    callback(Friend(uid, name, profileImageUrl)) // 콜백으로 친구 객체 반환
                } else {
                    callback(null) // 문서가 존재하지 않을 경우 null 반환
                }
            }
            .addOnFailureListener {
                callback(null) // 가져오기 실패 시 null 반환
            }
    }

    // 이메일을 통해 Firestore에서 친구를 추가하는 함수
    fun addFriendByEmail(currentUserId: String, email: String, callback: (Boolean) -> Unit) {
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val friendId = result.documents[0].id // 첫 번째 문서의 ID를 친구 ID로 사용
                    val friendName = result.documents[0].getString("name") ?: ""
                    val currentUserRef = db.collection("users").document(currentUserId)
                    val friendRef = db.collection("users").document(friendId)

                    val currentUserUpdate = currentUserRef.update("friends", FieldValue.arrayUnion(friendId))
                    val friendUpdate = friendRef.update("friends", FieldValue.arrayUnion(currentUserId))

                    // 현재 사용자 업데이트가 성공하면 친구 업데이트 수행
                    currentUserUpdate
                        .addOnSuccessListener {
                            friendUpdate
                                .addOnSuccessListener { callback(true) } // 친구 업데이트 성공 시 true 반환
                                .addOnFailureListener { callback(false) } // 친구 업데이트 실패 시 false 반환
                        }
                        .addOnFailureListener { callback(false) } // 현재 사용자 업데이트 실패 시 false 반환
                } else {
                    callback(false) // 이메일로 사용자를 찾을 수 없을 경우 false 반환
                }
            }
            .addOnFailureListener { callback(false) } // 쿼리 실패 시 false 반환
    }

    // Firestore에서 친구를 삭제하는 함수
    fun deleteFriend(currentUserId: String, friendId: String) {
        // 현재 사용자와 친구의 친구 목록에서 서로를 제거
        db.collection("users").document(currentUserId).update("friends", FieldValue.arrayRemove(friendId))
        db.collection("users").document(friendId).update("friends", FieldValue.arrayRemove(currentUserId))
    }

    // Firebase Storage에서 프로필 이미지를 가져오는 함수
    fun loadProfileImage(userId: String) {
        val storageRef = storage.reference.child("profiles/$userId.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            _profileImageUri.value = uri // 성공적으로 URI를 가져오면 상태 업데이트
        }.addOnFailureListener {
            _profileImageUri.value = null // 실패 시 null로 설정
        }
    }

    // ViewModel이 클리어될 때 리스너 제거
    override fun onCleared() {
        super.onCleared()
        friendsListener?.remove() // Firestore 리스너 제거
    }
}
