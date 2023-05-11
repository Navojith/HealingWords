package com.example.healingwords

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.healingwords.databinding.ActivityRegisterDoctorPageBinding
import com.example.healingwords.models.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterDoctorPage : AppCompatActivity() {

    lateinit var binding: ActivityRegisterDoctorPageBinding
    lateinit var docDatabaseRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDoctorPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.registerDocBtn.setOnClickListener {
            val username = binding.edtDocUsernameRegister.text.toString()
            val email = binding.edtDocEmail.text.toString()
            val password = binding.edtDocPasswordRegister.text.toString()
            val name = binding.edtDocNameRegister.text.toString()
            val rePassword = binding.edtDocRePasswordRegister.text.toString()
            val title = binding.edtDocTitle.text.toString()
            val regProgressBar = binding.registerProgress
            var rating = 0

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()) {
                val passwordStatus = isValidPassword(password)
                if (passwordStatus) {
                    if(password == rePassword){
                        regProgressBar.visibility = View.VISIBLE
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                                    if(currentFirebaseUser != null) {
                                        docDatabaseRef = FirebaseDatabase.getInstance().getReference("Doctors")
                                        val doc = Doctor(currentFirebaseUser.uid,username, email, name, title, "", rating.toString())
                                        docDatabaseRef.child(currentFirebaseUser.uid).setValue(doc).addOnSuccessListener {
                                            sendToDocMain(currentFirebaseUser.uid)
                                        }.addOnFailureListener {
                                            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                                        }

                                    }


                                } else {
                                    val errorMessage = task.exception?.message
                                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                                }

                                regProgressBar.visibility = View.INVISIBLE
                            }
                    }else{
                        Toast.makeText(this, "passwords doesn't match", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "password should contain at-least 8 letters including uppercase & lowercase & digits & special characters",Toast.LENGTH_LONG).show()
                }
            }else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG)
            }
        }


    }

    private fun sendToDocMain(uid: String) {
        val intent = Intent(this, DocMainUI::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }
}