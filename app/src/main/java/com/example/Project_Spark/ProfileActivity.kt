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
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance().currentUser!!
        storageRef = FirebaseStorage.getInstance().reference

        binding.btnChooseImage.setOnClickListener {
            chooseImage()
        }

        binding.btnSaveProfile.setOnClickListener {
            uploadImage()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                imageUri = data.data
                Picasso.get().load(imageUri).into(binding.imgProfile)
            }
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val ref = storageRef.child("profiles/${user.uid}")
            ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveProfile(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Image Upload Failed", e)
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

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
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Profile Save Failed", e)
                Toast.makeText(this, "Failed to Save Profile", Toast.LENGTH_SHORT).show()
            }
    }
}
