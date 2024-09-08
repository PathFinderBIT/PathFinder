package com.project.pathfinder.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.pathfinder.MainActivity
import com.project.pathfinder.databinding.ActivitySignupBinding

class SignUpActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbRef = database.reference
        binding.btnSignup.setOnClickListener {
            val emaill: String = binding.editUsermailInput.text.toString().trim { it <= ' ' }
            val pass: String = binding.editPasswordInput.text.toString().trim { it <= ' ' }
            val fullname: String = binding.editFullnameInput
                .text.toString().trim { it <= ' ' }
            // if format of email doesn't matches return null
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                binding.editUsermail.setError("Invalid Email")
                binding.editUsermailInput.isFocusable = true
            } else {
                binding.editUsermail.setError("")
                binding.editUsermailInput.isFocusable = false
            }
            if (pass == "") {
                binding.editPassword.setError("Invalid Password")
                binding.editPasswordInput.isFocusable = true
            } else {
                binding.editPassword.setError("")
                binding.editPasswordInput.isFocusable = false
            }
            if (fullname == "") {
                binding.editFullname.setError("Field Can't Be Empty")
                binding.editFullnameInput.isFocusable = true
            } else {
                binding.editFullname.setError("")
                binding.editFullnameInput.isFocusable = false
            }
            signupUser(emaill, pass,fullname)
        }

    }
    private fun signupUser(email: String, pass: String,fullname: String) {
        showProgress()

        // sign in with email and password after authenticating
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //loadingBar!!.dismiss()
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(fullname)
                    .build()

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@SignUpActivity, "Registration Successful", Toast.LENGTH_SHORT)
                                .show()
                            val mainIntent = Intent(this@SignUpActivity, MainActivity::class.java)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(mainIntent)
                            finish()
                        }
                    }
                hideProgress()
//                if (task.result.additionalUserInfo!!.isNewUser) {
//                    val email = user!!.email
//                    val uid = user.uid
//                    val hashMap = HashMap<Any, String?>()
//                    hashMap["email"] = email
//                    hashMap["uid"] = uid
//                    hashMap["name"] = fullname
//                    hashMap["phone"] = phone
//                    hashMap["image"] = ""
//                    val database = FirebaseDatabase.getInstance()
//
//                    // store the value in Database in "Users" Node
//                    val reference = database.getReference("Users")
//
//                    // storing the value in Firebase
//                    reference.child(uid).setValue(hashMap)
//                }


            } else {
                //  loadingBar!!.dismiss()
                Toast.makeText(this@SignUpActivity, "Login Failed", Toast.LENGTH_LONG).show()
                hideProgress()
            }
        }.addOnFailureListener {
            // loadingBar!!.dismiss()
            Toast.makeText(this@SignUpActivity, "Error Occured", Toast.LENGTH_LONG).show()
            hideProgress()
        }
    }
    private fun hideProgress(){
        binding.loader.visibility = View.GONE
        binding.btnSignup.visibility = View.VISIBLE
    }
    private fun showProgress(){
        binding.btnSignup.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE

    }

}