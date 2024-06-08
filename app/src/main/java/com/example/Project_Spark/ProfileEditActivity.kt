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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.Project_Spark.ui.components.BottomNavigationBar
import com.example.Project_Spark.ui.theme.ProjectSparkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

const val TAG1 = "ProfileEditActivity"

class ProfileEditActivity : ComponentActivity() {
    private lateinit var storageReference: StorageReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        storageReference = FirebaseStorage.getInstance().reference.child("UserProfile")

        setContent {
            ProjectSparkTheme {
                ProfileEditScreenWrapper(currentUser, storageReference)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreenWrapper(currentUser: FirebaseUser, storageReference: StorageReference) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필 수정") },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, HomeActivity_meeting::class.java)
                        context.startActivity(intent)
                        if (context is Activity) {
                            (context as Activity).finish()
                        }
                    }) {
                        Icon(painterResource(id = R.drawable.expand_left), contentDescription = null)
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar() }
    ) {
        ProfileEditScreen(currentUser, storageReference, Modifier.padding(it))
    }
}

@Composable
fun ProfileEditScreen(currentUser: FirebaseUser, storageReference: StorageReference, modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var isIntroverted by remember { mutableStateOf(true) }
    var major by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(currentUser.uid) {
        FirebaseFirestore.getInstance().collection("profiles").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                name = document.getString("name") ?: ""
                bio = document.getString("bio") ?: ""
                isIntroverted = document.getBoolean("isIntrovert") ?: true
                major = document.getString("major") ?: ""
                studentId = document.getString("studentId") ?: ""
                imageUri = document.getString("imageUrl")?.let { Uri.parse(it) }
            }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = uri
        }
    }

    fun uploadImageAndSaveProfile() {
        if (imageUri != null) {
            val ref = storageReference.child(currentUser.uid)
            val uploadTask = ref.putFile(imageUri!!)

            uploadTask.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveProfileData(currentUser, name, bio, isIntroverted, major, studentId, uri.toString(), context)
                }.addOnFailureListener { e ->
                    Log.e(TAG1, "Failed to get download URL", e)
                    Toast.makeText(context, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e(TAG1, "Image Upload Failed", e)
                Toast.makeText(context, "Image Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            saveProfileData(currentUser, name, bio, isIntroverted, major, studentId, "", context)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
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

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("프로필사진 선택")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("자기소개") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("성격: ")
            RadioButton(
                selected = isIntroverted,
                onClick = { isIntroverted = true }
            )
            Text("내향형")
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = !isIntroverted,
                onClick = { isIntroverted = false }
            )
            Text("외향형")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = major,
            onValueChange = { major = it },
            label = { Text("학과") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("학번") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { uploadImageAndSaveProfile() }) {
            Text("프로필 수정 완료")
        }
    }
}

fun saveProfileData(
    currentUser: FirebaseUser,
    name: String,
    bio: String,
    isIntroverted: Boolean,
    major: String,
    studentId: String,
    imageUrl: String,
    context: Context
) {
    val profile = hashMapOf(
        "name" to name,
        "bio" to bio,
        "isIntrovert" to isIntroverted,
        "major" to major,
        "studentId" to studentId,
        "email" to currentUser.email,
        "imageUrl" to imageUrl
    )

    val db = FirebaseFirestore.getInstance()
    db.collection("profiles").document(currentUser.uid)
        .set(profile)
        .addOnSuccessListener {
            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, HomeActivity_meeting::class.java)
            context.startActivity(intent)
            if (context is Activity) {
                (context as Activity).finish()
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG1, "Profile Update Failed", e)
            Toast.makeText(context, "Failed to Update Profile", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    ProjectSparkTheme {
        val dummyUser = FirebaseAuth.getInstance().currentUser!!
        val dummyStorageRef = FirebaseStorage.getInstance().reference.child("UserProfile")
        ProfileEditScreen(dummyUser, dummyStorageRef)
    }
}
