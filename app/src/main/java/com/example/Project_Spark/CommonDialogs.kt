package com.example.Project_Spark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.Project_Spark.model.Friend

@Composable
fun FriendSelectionDialog(
    friendsList: List<Friend>,
    onDismiss: () -> Unit,
    onConfirm: (List<Friend>) -> Unit
) {
    var selectedFriends by remember { mutableStateOf(emptyList<Friend>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("친구 선택") },
        text = {
            LazyColumn {
                items(friendsList) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFriends = if (selectedFriends.contains(friend)) {
                                    selectedFriends - friend
                                } else {
                                    selectedFriends + friend
                                }
                            }
                            .padding(8.dp)
                            .background(if (selectedFriends.contains(friend)) Color.LightGray else Color.Transparent)
                    ) {
                        Image(
                            painter = rememberImagePainter(data = friend.profileImageUrl ?: R.drawable.defaultprofile),
                            contentDescription = "Profile Image",
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            friend.name,
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedFriends) }) {
                Text("확인")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
