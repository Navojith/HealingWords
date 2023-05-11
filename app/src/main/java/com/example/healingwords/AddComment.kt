package com.example.healingwords

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.healingwords.databinding.ActivityAddCommentBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.widget.Toolbar

class AddComment : AppCompatActivity() {

    private lateinit var postId: String
    private lateinit var userId: String
    private lateinit var binding: ActivityAddCommentBinding
    private lateinit var database: DatabaseReference
    private lateinit var ToolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolBar = findViewById(R.id.addCommentToolbar)
        setSupportActionBar(ToolBar)
        supportActionBar?.setTitle("Add Comment")

        postId = intent.getStringExtra("postId") ?: ""
        userId = intent.getStringExtra("userId") ?: ""

        binding.Add.setOnClickListener{

            val comment = binding.commentBody.text.toString()

            //validations
            if (comment.isEmpty()){
                binding.commentBody.error = "comment required"
                return@setOnClickListener
            }


            database = FirebaseDatabase.getInstance().getReference("comments")

            val commentId = database.push().key
            val Comment = Comment(commentId, comment, userId, postId )

            //move to database
            database.child(commentId!!).setValue(Comment).addOnSuccessListener {

                binding.commentBody.text.clear()

                Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener{
                Toast.makeText(this, "Fail to add", Toast.LENGTH_SHORT).show()

            }
            val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra("postId", postId)
            //intent.putExtra("userId", userId)
            startActivity(intent)
            finish()

        }
    }
}