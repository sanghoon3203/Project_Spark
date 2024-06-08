package com.example.Project_Spark

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.example.Project_Spark.model.Friend
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                val navController = rememberNavController()
                TeamCreateScreen(navController)
            }
        }
    }
}

@Composable
fun TeamCreateScreen(navController: NavController) {
    val teamName = remember { mutableStateOf("") }
    val department = remember { mutableStateOf("") }
    val teamDescription = remember { mutableStateOf("") }
    val teamMembers = remember { mutableStateListOf<Friend>() }
    val showMemberDialog = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    if (userId.isEmpty()) {
        Toast.makeText(context, "Failed to get user ID", Toast.LENGTH_SHORT).show()
        return
    }

    val friendsList = remember { mutableStateListOf<Friend>() }
    LaunchedEffect(userId) {
        fetchFriends { friends ->
            friendsList.addAll(friends)
        }
    }

    if (showMemberDialog.value) {
        MemberSelectionDialog(friendsList, onDismiss = { showMemberDialog.value = false }) { selectedMembers ->
            selectedMembers.forEach { memberId ->
                val member = friendsList.find { it.id == memberId }
                if (member != null && !teamMembers.contains(member)) {
                    teamMembers.add(member)
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("팀을 생성하시겠습니까?", fontFamily = FontFamily(Font(R.font.applesdgothicneobold))) },
            confirmButton = {
                TextButton(onClick = {
                    val memberIds = teamMembers.map { it.id }
                    createTeam(userId, teamName.value, department.value, teamDescription.value, memberIds) { success ->
                        if (success) {
                            Toast.makeText(context, "팀이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                            showDialog.value = false
                        } else {
                            Toast.makeText(context, "팀 생성 실패", Toast.LENGTH_SHORT).show()
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

    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            CustomTextField(
                value = teamName.value,
                onValueChange = { teamName.value = it },
                label = "팀 이름",
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = department.value,
                onValueChange = { department.value = it },
                label = "학과",
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = teamDescription.value,
                onValueChange = { teamDescription.value = it },
                label = "팀 소개",
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "팀원 추가", fontFamily = fontFamily)
                IconButton(onClick = { showMemberDialog.value = true }) {
                    Icon(painterResource(id = R.drawable.create), contentDescription = "Add")
                }
            }

            teamMembers.forEach { member ->
                Text(text = member.name, fontFamily = fontFamily)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    showDialog.value = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF7DD8C6))
            ) {
                Text(text = "팀 생성", fontFamily = fontFamily)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavigationBar()
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            TeamCreateTopBar(navController = navController)
        }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, fontFamily: FontFamily) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = fontFamily) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily)
    )
}

@Composable
fun TeamCreateTopBar(navController: NavController) {
    val context = LocalContext.current
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))
    TopAppBar(
        title = { Text("팀 생성", fontFamily = fontFamily) },
        navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, HomeActivity_meeting::class.java)
                context.startActivity(intent)
            }) {
                Icon(painterResource(id = R.drawable.expand_left), contentDescription = "Back")
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = Color(0xFF7DD8C6),
        actions = {},
        elevation = 8.dp
    )
}

@Composable
fun FriendItem(friend: Friend, isSelected: Boolean, onSelect: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelect
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = rememberAsyncImagePainter(model = friend.profileImageUrl ?: R.drawable.user_circle),
            contentDescription = "Profile Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = friend.name,
            fontSize = 20.sp,
            lineHeight = 21.sp,
            fontFamily = FontFamily(Font(R.font.applesdgothicneobold)),
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun MemberSelectionDialog(friends: List<Friend>, onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    val selectedMembers = remember { mutableStateListOf<String>() }
    val fontFamily = FontFamily(Font(R.font.applesdgothicneobold))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.background,
            contentColor = contentColorFor(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "팀원 선택", style = MaterialTheme.typography.h6, fontFamily = fontFamily)

                LazyColumn {
                    items(friends) { friend ->
                        val isSelected = selectedMembers.contains(friend.id)
                        FriendItem(
                            friend = friend,
                            isSelected = selectedMembers.contains(friend.id),
                            onSelect = { isSelected ->
                                if (isSelected) {
                                    if (selectedMembers.size < 3) {
                                        selectedMembers.add(friend.id)
                                    }
                                } else {
                                    selectedMembers.remove(friend.id)
                                }
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "취소", fontFamily = fontFamily)
                    }
                    TextButton(onClick = { onConfirm(selectedMembers); onDismiss() }) {
                        Text(text = "등록", fontFamily = fontFamily)
                    }
                }
            }
        }
    }
}

fun fetchFriends(onComplete: (List<Friend>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            val friends = document["friends"] as? List<String> ?: listOf()
            val friendsList = mutableListOf<Friend>()

            friends.forEach { friendId ->
                db.collection("profiles").document(friendId).get()
                    .addOnSuccessListener { friendDocument ->
                        val name = friendDocument.getString("name") ?: ""
                        val imageUrl = friendDocument.getString("imageUrl")
                        val friend = Friend(id = friendId, name = name, profileImageUrl = imageUrl)
                        if (friend != null) {
                            friendsList.add(friend)
                        }
                        if (friendsList.size == friends.size) {
                            onComplete(friendsList)
                        }
                    }
            }
        }
}

fun createTeam(userId: String, teamName: String, department: String, teamDescription: String, teamMembers: List<String>, onComplete: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val team = hashMapOf(
        "name" to teamName,
        "department" to department,
        "description" to teamDescription,
        "members" to teamMembers
    )

    db.collection("teams").add(team)
        .addOnSuccessListener { documentReference ->
            db.collection("users").document(userId).update("myteams", FieldValue.arrayUnion(documentReference.id))
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        }
        .addOnFailureListener {
            onComplete(false)
        }
}
