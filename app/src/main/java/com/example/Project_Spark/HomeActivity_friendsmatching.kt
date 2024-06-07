package com.example.Project_Spark

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeActivity_friendsmatching : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                HomeScreen()
            }
        }
    }
}

// Firebase Firestore 데이터 모델
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val major: String = "",
    val imageUrl: String = ""
)

// HomeScreen 구성
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var friendsRecommendList by remember { mutableStateOf(listOf<UserProfile>()) }

    LaunchedEffect(currentUser) {
        val allUserUids = firestore.collection("users").get().await().documents.map { it.id }
        val friendsList = firestore.collection("users").document(currentUser!!.uid).get().await()
            .get("friends") as? List<String> ?: emptyList()
        val filteredUids = allUserUids.filter { it !in friendsList }
        val shuffledUids = filteredUids.shuffled()

        val profiles = shuffledUids.map { uid ->
            val document = firestore.collection("profiles").document(uid).get().await()
            UserProfile(
                uid = uid,
                name = document.getString("name") ?: "",
                major = document.getString("major") ?: "",
                imageUrl = document.getString("imageUrl") ?: ""
            )
        }
        friendsRecommendList = profiles
    }

    Scaffold(
        topBar = {
            TopBar(
                onFriendClick = {},
                onMeetingClick = {
                    val intent = Intent(context, HomeActivity_meeting::class.java)
                    context.startActivity(intent)
                },
                onBellClick = {
                    val intent = Intent(context, HomeActivity_meeting::class.java)
                    context.startActivity(intent)
                },
                onFilterClick = {
                    val intent = Intent(context, HomeActivity_meeting::class.java)
                    context.startActivity(intent)
                }
            )
        },
        bottomBar = { BottomNavigationBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                FriendsBanner()
                FriendsRecommendList(friendsRecommendList)
            }
        }
    )
}

@Composable
fun FriendsRecommendList(friendsRecommendList: List<UserProfile>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(friendsRecommendList.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pair.forEach { profile ->
                    FriendCard(profile)
                }
            }
        }
    }
}
@Composable
fun FriendCard(profile: UserProfile) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(170.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFE0B2))
            .clickable { expanded = !expanded }
            .shadow(10.dp, RoundedCornerShape(20.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(profile.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = profile.major,
                textAlign = TextAlign.Center
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically) + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically) + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { expanded = false }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Image(
                        painter = rememberImagePainter(profile.imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showDialog = true
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF03DAC5))
                    ) {
                        Text(text = "+", fontSize = 24.sp)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("친구를 추가하겠습니까?", fontFamily = FontFamily(Font(R.font.applesdgothicneobold))) },
            confirmButton = {
                TextButton(onClick = {
                    // Firebase Firestore에서 친구 추가
                    val currentUserUid = auth.currentUser?.uid
                    if (currentUserUid != null) {
                        val userDocument = firestore.collection("users").document(currentUserUid)
                        userDocument.update("friends", FieldValue.arrayUnion(profile.uid))
                            .addOnSuccessListener {
                                Toast.makeText(context, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                                showDialog = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "친구 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                showDialog = false
                            }
                    }
                }) {
                    Text("생성", fontFamily = FontFamily(Font(R.font.applesdgothicneobold)))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소", fontFamily = FontFamily(Font(R.font.applesdgothicneobold)))
                }
            }
        )
    }
}


@Composable
fun showAddFriendDialog(context: android.content.Context, auth: FirebaseAuth, firestore: FirebaseFirestore, friendUid: String) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("친구를 추가하겠습니까?", fontFamily = FontFamily(Font(R.font.applesdgothicneobold))) },
            confirmButton = {
                TextButton(onClick = {
                    // Firebase Firestore에서 친구 추가
                    val currentUserUid = auth.currentUser?.uid
                    if (currentUserUid != null) {
                        val userDocument = firestore.collection("users").document(currentUserUid)
                        userDocument.update("friends", FieldValue.arrayUnion(friendUid))
                            .addOnSuccessListener {
                                Toast.makeText(context, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                                showDialog.value = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "친구 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                showDialog.value = false
                            }
                    }
                }) {
                    Text("생성", fontFamily = FontFamily(Font(R.font.applesdgothicneobold)))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("취소", fontFamily = FontFamily(Font(R.font.applesdgothicneobold)))
                }
            }
        )
    }
}

@Composable
fun TopBar(
    onFriendClick: () -> Unit,
    onMeetingClick: () -> Unit,
    onBellClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            "친구",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraLight,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "미팅",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            color = Color.Gray,
            modifier = Modifier.clickable(onClick = onMeetingClick)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun FriendsBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.friendbanner),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
