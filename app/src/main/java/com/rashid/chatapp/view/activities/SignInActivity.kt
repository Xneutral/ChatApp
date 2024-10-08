package com.rashid.chatapp.view.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.rashid.chatapp.R
import com.rashid.chatapp.databinding.ActivitySignInBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.model.User

class SignInActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding

    private lateinit var firebaseAuth : FirebaseAuth

    private lateinit var firestore: FirebaseFirestore

    private var isUserLoggingIn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupUI()
        handleSignIn()

    }

    private fun setupUI() {
        if (isUserLoggingIn){
            binding.registerLl.visibility = View.GONE
            binding.loginLl.visibility = View.VISIBLE
        }else{
            binding.registerLl.visibility = View.VISIBLE
            binding.loginLl.visibility = View.GONE
        }
    }

    private fun handleSignIn() {
        binding.loginBtn.setOnClickListener {
            checkCredentials()
        }

        binding.registerBtn.setOnClickListener {
            registerAndLogin()
        }

        binding.registerTv.setOnClickListener {
            isUserLoggingIn = false
            setupUI()
        }


        binding.loginTv.setOnClickListener {
            isUserLoggingIn = true
            setupUI()
        }


    }

    private fun checkCredentials() {
        val loginEmail = binding.loginEmail.editText?.text.toString()
        val loginPass = binding.loginPassword.editText?.text.toString()
        if (loginPass.isBlank() || loginPass.isEmpty()
            || loginEmail.isBlank() || loginEmail.isEmpty()
        ){
            Toast.makeText(this,"Provide All Credentials",Toast.LENGTH_SHORT).show()
            return
        }
        binding.registerProgress.visibility = View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    binding.registerProgress.visibility = View.GONE

                    Toast.makeText(this,"User Logged In Successful",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->

            }
    }

    private fun registerAndLogin() {
        val registerEmail = binding.registerEmail.editText?.text.toString()
        val registerPass = binding.registerPassword.editText?.text.toString()
        val registerUserName = binding.registerUserName.editText?.text.toString()
        if (registerPass.isBlank() || registerPass.isEmpty()
            || registerEmail.isBlank() || registerEmail.isEmpty()
            || registerUserName.isEmpty() || registerUserName.isBlank()
            ){
            Toast.makeText(this,"Provide All Credentials",Toast.LENGTH_SHORT).show()
            return
        }
        binding.registerProgress.visibility = View.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(registerEmail, registerPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    binding.registerProgress.visibility = View.GONE
                    val uId = task.result.user?.uid ?: return@addOnCompleteListener
                    val user = User(uId,registerUserName, registerEmail, registerPass)
                    saveToFireBaseDb(user)
                    Toast.makeText(this,"User Logged In Successful",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->

            }
    }

    private fun saveToFireBaseDb(user: User) {
        firestore.collection(Constants.UsersPath)
            .add(user)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                Toast.makeText(this, "User Registration failed", Toast.LENGTH_SHORT).show()
            }
    }
}