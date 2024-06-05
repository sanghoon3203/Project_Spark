package com.example.Project_Spark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmedMatchingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectSparkTheme {
                val navController = rememberNavController()
                ConfirmedMatchingsScreen(navController)
            }
        }
    }
}

@Composable
fun ConfirmedMatchingsScreen(navController: NavController, viewModel: ConfirmedMatchingsViewModel = hiltViewModel()) {
    val confirmedMatchings by viewModel.confirmedMatchings.collectAsState()
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
        ) {
            Text(text = "확정된 매칭", style = MaterialTheme.typography.h6, fontFamily = fontFamily)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(confirmedMatchings) { matching ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "팀 이름: ${matching.teamName}", fontFamily = fontFamily, fontSize = 16.sp)
                            Text(text = "날짜: ${matching.date}", fontFamily = fontFamily, fontSize = 16.sp)
                            Text(text = "멤버: ${matching.members.joinToString(", ")}", fontFamily = fontFamily, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class ConfirmedMatchingsViewModel @Inject constructor() : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _confirmedMatchings = MutableStateFlow<List<MeetingReservation>>(emptyList())
    val confirmedMatchings: StateFlow<List<MeetingReservation>> = _confirmedMatchings

    init {
        fetchConfirmedMatchings()
    }

    private fun fetchConfirmedMatchings() {
        val userId = auth.currentUser?.uid ?: return
        db.collectionGroup("Matchings")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { documents ->
                val matchings = documents.map { doc ->
                    MeetingReservation(
                        teamName = doc.getString("teamName") ?: "",
                        date = doc.getString("date") ?: "",
                        members = doc.get("members") as? List<String> ?: emptyList()
                    )
                }
                _confirmedMatchings.value = matchings
            }
    }
}
