package com.project.pathfinder.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.pathfinder.MainActivity
import com.project.pathfinder.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

     private lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.rootLayout)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbRef = database.reference
        binding.lin2.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        binding.tvFPassword.setOnClickListener {
            showRecoverPasswordDialog()
        }
        binding.btnLogin.setOnClickListener {
            val emaill: String = binding.editUsermailInput.text.toString().trim { it <= ' ' }
            val pass: String = binding.editPasswordInput.text.toString().trim { it <= ' ' }

            // if format of email doesn't matches return null
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                binding.editUseremail.setError("Invalid Email")
                binding.editUsermailInput.isFocusable = true
            } else {
                binding.editUseremail.setError("")
                binding.editUsermailInput.isFocusable = false
            }
            if (pass == "") {
                binding.editPassword.setError("Invalid Password")
                binding.editPasswordInput.isFocusable = true
            } else {
                binding.editPassword.setError("")
                binding.editPasswordInput.isFocusable = false
            }
            loginUser(emaill, pass)
        }
    }
    private fun loginUser(email: String, pass: String) {
        showProgress()

        // sign in with email and password after authenticating
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //loadingBar!!.dismiss()
               // val user = auth.currentUser
//                if (task.result.additionalUserInfo!!.isNewUser) {
////                    val email = user!!.email
////                    val uid = user.uid
////                    val hashMap = HashMap<Any, String?>()
////                    hashMap["email"] = email
////                    hashMap["uid"] = uid
////                    hashMap["name"] = ""
////                    hashMap["onlineStatus"] = "online"
////                    hashMap["typingTo"] = "noOne"
////                    hashMap["phone"] = ""
////                    hashMap["image"] = ""
////                    hashMap["cover"] = ""
////                    val database = FirebaseDatabase.getInstance()
////
////                    // store the value in Database in "Users" Node
////                    val reference = database.getReference("Users")
////
////                    // storing the value in Firebase
////                    reference.child(uid).setValue(hashMap)
//                }
//                Toast.makeText(
//                    this@LoginActivity,
//                    "Registered User " + user!!.email,
//                    Toast.LENGTH_LONG
//                ).show()
                val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
                hideProgress()
            } else {
              //  loadingBar!!.dismiss()
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                hideProgress()
            }
        }.addOnFailureListener {
           // loadingBar!!.dismiss()
            Toast.makeText(this@LoginActivity, "Error Occured", Toast.LENGTH_LONG).show()
            hideProgress()
        }
    }

    private fun showRecoverPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recover Password")
        val linearLayout = LinearLayout(this)
        val emailet = EditText(this) //write your registered email
        emailet.hint ="Enter your email addresss"
        emailet.minEms = 16
        emailet.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        linearLayout.addView(emailet)
        linearLayout.setPadding(10, 10, 10, 10)
        builder.setView(linearLayout)
        builder.setPositiveButton("Recover") { dialog, which ->
            val emaill: String = emailet.text.toString().trim { it <= ' ' }
            beginRecovery(emaill) //send a mail on the mail to recover password
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    private fun beginRecovery(emaill: String) {

        // send reset password email
        auth.sendPasswordResetEmail(emaill).addOnCompleteListener { task ->
           // loadingBar!!.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this@LoginActivity, "Check your mail for password rest link", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@LoginActivity, "Error Occured", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            //loadingBar!!.dismiss()
            Toast.makeText(this@LoginActivity, "Error Failed", Toast.LENGTH_LONG).show()
        }
    }
    private fun hideProgress(){
        binding.loader.visibility = View.GONE
        binding.btnLogin.visibility = View.VISIBLE
    }
    private fun showProgress(){
        binding.btnLogin.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE

    }
}