package com.example.healingwords

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.healingwords.databinding.FragmentDocProfileBinding
import com.example.healingwords.models.Review
import com.google.android.gms.common.SignInButton.ButtonSize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.round




class DocProfile : Fragment() {

    private lateinit var btnViewBlog : Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var tvName: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvTitle: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var uid: String
    private lateinit var tvNoOfReviews: TextView
    private lateinit var database : DatabaseReference
    private lateinit var reviewDbRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser

        val view =  inflater.inflate(R.layout.fragment_doc_profile, container, false)


        tvName = view.findViewById(R.id.tvDocName)

        tvRating = view.findViewById(R.id.tvTotalRatingDocProfile)
        tvTitle = view.findViewById(R.id.tvDocTitle)
        tvNoOfReviews = view.findViewById(R.id.docProfileNoOfReviews)
        btnDelete = view.findViewById(R.id.btnDeleteDocProfile)
        btnEdit = view.findViewById(R.id.btnEditDocProfile)

        //edit profile
        btnEdit.setOnClickListener {
            val intent =Intent(requireActivity(), EditDocProfile::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        //delete profile
        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Confirm Delete")
            builder.setMessage("Do you want to delete your account?")

            builder.setPositiveButton("Delete") { _, _ ->
                var docUid = currentFirebaseUser!!.uid
                FirebaseDatabase.getInstance().getReference("Doctors").child(docUid).removeValue().addOnSuccessListener{
                    Toast.makeText(requireActivity(),"Deleted Successfully",Toast.LENGTH_LONG )
                    var intent = Intent(requireActivity(), LoginPage::class.java)
                    val currentUser =FirebaseAuth.getInstance().currentUser
                    mAuth.signOut()
                    currentUser?.delete()
                    startActivity(intent)
                    requireActivity().finish()
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.create().show()


        }
        btnViewBlog = view.findViewById(R.id.btnViewBlog)

        uid = currentFirebaseUser!!.uid

        if(uid.isNotEmpty()) {
            readData(uid)
            setTotalRating(uid)
        }

        btnViewBlog.setOnClickListener {
            val intent = Intent(requireContext(),AllDocBlogs::class.java)
            intent.putExtra("docUid",uid)
            startActivity(intent)


        }



        return view
    }

    private fun readData(uid: String) {
        database = FirebaseDatabase.getInstance().getReference("Doctors")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()) {
                var name = it.child("name").value
                var title = it.child("title").value


                tvName.text = name.toString()

                tvTitle.text = title.toString()
                btnDelete.text = "Delete"
                btnEdit.text = "Edit"

            }else {
                Toast.makeText(requireActivity(), "User doesn't exists", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_LONG).show()
        }
    }


    private fun setTotalRating(docUid: String) {
        var totStars = 0.0
        var totGivenStars = 0.0
        var noOfReviews = 0.0
        Log.d("i-totStars", totStars.toString())
        Log.d("i-totGivenStars", totGivenStars.toString())
        Log.d("i-noOfReviews", noOfReviews.toString())
        reviewDbRef =  FirebaseDatabase.getInstance().getReference("Reviews")
        reviewDbRef.addValueEventListener(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot){
                if (snapshot.exists()) {
                    for (reviewSnapshot in snapshot.children) {
                        val review = reviewSnapshot.getValue(Review::class.java)
                        Log.d("uid", (review!!.docUid == docUid).toString())

                        if (review!!.docUid == docUid) {
                            noOfReviews += 1.0
                            totStars += 5.0
                            totGivenStars += review.noOfStars!!.toDouble()

                        }

                    }

                }
                val finalRating = String.format("%.1f", ((totGivenStars / (noOfReviews)))).toDouble()
                if (finalRating.isNaN()) {
                    tvRating.text = "0.0/5"
                } else {
                    tvRating.text = "$finalRating/5"
                }
                if(noOfReviews.toInt() == 0){
                    tvNoOfReviews.text = "( ${noOfReviews.toInt()} Review )"
                } else {
                    tvNoOfReviews.text = "( ${noOfReviews.toInt()} Reviews )"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }


}