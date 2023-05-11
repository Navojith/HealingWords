package com.example.healingwords

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewPostActivity : AppCompatActivity() {

    private lateinit var newPostToolbar: Toolbar
    private lateinit var newPostDesc: EditText
    private lateinit var newPostBtn: Button

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        newPostToolbar = findViewById(R.id.newPostToolbar)
        setSupportActionBar(newPostToolbar)
        supportActionBar?.setTitle("Add new post")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        newPostDesc = findViewById(R.id.newPostDesc)
        newPostBtn = findViewById(R.id.postBtn)

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser?.uid ?: ""



        newPostBtn.setOnClickListener {
            val desc = newPostDesc.text.toString()
            if (desc.isNotEmpty()) {
                userId?.let { id ->
                    val timestamp = System.currentTimeMillis()
                    val postMap = hashMapOf(
                        "desc" to desc,
                        "userId" to id,
                        "timestamp" to timestamp
                    )
                    firebaseFirestore.collection("Posts").add(postMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                            val mainIntent = Intent(this, MainActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }else {
                Toast.makeText(this,"Empty description", Toast.LENGTH_LONG).show()
            }
        }

    }
}