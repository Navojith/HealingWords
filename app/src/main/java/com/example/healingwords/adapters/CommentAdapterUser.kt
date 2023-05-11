package com.example.healingwords.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.healingwords.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class CommentAdapterUser(private val postId: String, private val commentList: ArrayList<Comment>) : RecyclerView.Adapter<CommentAdapterUser.MyViewHolder> (){

    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent,false)

        database = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentuser = firebaseAuth.currentUser?.uid
        val currentitem = commentList[position]
        val userId = currentitem.userId.toString()
        val commentId = currentitem.commentId

        //invisible the edit button if comment is not from current user
        if(currentuser.toString() != userId){
            holder.commentEditImg.visibility = View.INVISIBLE
            //holder.commentDeleteImg.visibility = View.INVISIBLE
        }
        if(currentuser.toString() == userId){
            holder.userId.setTextColor(holder.userId.context.getColor(R.color.success))
        }

        database.child("Users").child(userId).get().addOnCompleteListener{ task ->       //get user id
            if(task.isSuccessful){
                val dataSnapshot = task.result
                if(dataSnapshot.exists()){
                    val username = dataSnapshot.child("username").value.toString()
                    holder.userId.text = username
                }else{
                    database.child("Doctors").child(userId).get().addOnCompleteListener{ task ->     //get doctor id
                        if(task.isSuccessful){
                            val dataSnapshot = task.result
                            if(dataSnapshot.exists()){
                                val username = dataSnapshot.child("username").value.toString()
                                holder.userId.text = username
                            }
                        }
                    }
                }
            }
        }

        //holder.userId.text = username
        holder.commentBody.text = currentitem.commentBody

        holder.commentEditImg.setOnClickListener {
            val intent = Intent(holder.itemView.context, CommentUpdate::class.java)
            intent.putExtra("commentId", commentId)
            holder.itemView.context.startActivity(intent)
        }

        //delete the comment
        holder.commentDeleteImg.setOnClickListener {
            val builder = AlertDialog.Builder(holder.contexts)
            builder.setTitle("Delete comment")
            builder.setMessage("Are you sure want to delete comment?")
            builder.setPositiveButton("Yes"){dialog, which ->
                commentId?.let { id ->
                    database.child("comments").child(id).removeValue().addOnSuccessListener {
                        Toast.makeText(holder.contexts, "comment Deleted", Toast.LENGTH_LONG).show()
                        val intent = Intent(holder.contexts, ViewComments::class.java)
                        intent.putExtra("postId", postId)
                        holder.contexts.startActivity(intent)
                        // Finish the current activity

                        if (holder.contexts is Activity) {
                            (holder.contexts as Activity).finish()
                        }
                    }
                }
            }
            builder.setNegativeButton("No"){ dialog, which ->
                //user click no button
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }



    override fun getItemCount(): Int {
        return commentList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contexts: Context = itemView.context
        val userId : TextView = itemView.findViewById(R.id.tvname)
        val commentBody : TextView = itemView.findViewById(R.id.tvcomment)
        val commentEditImg : ImageView = itemView.findViewById(R.id.commentEditImg)
        val commentDeleteImg: ImageView = itemView.findViewById(R.id.commentDeleteImg)
    }

}