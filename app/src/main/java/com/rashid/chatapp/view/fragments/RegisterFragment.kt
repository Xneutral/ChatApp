package com.rashid.chatapp.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rashid.chatapp.databinding.FragmentRegisterBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.model.User
import com.rashid.chatapp.view.activities.MainActivity

class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClickListener()
    }

    private fun handleClickListener() {
        binding.registerBtn.setOnClickListener {
            registerAndLogin()
        }


        binding.loginTv.setOnClickListener {
           findNavController().popBackStack()
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
            Toast.makeText(context,"Provide All Credentials", Toast.LENGTH_SHORT).show()
            return
        }
        binding.registerProgress.visibility = View.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(registerEmail, registerPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    binding.registerProgress.visibility = View.GONE
                    val uId = task.result.user?.uid ?: return@addOnCompleteListener
                    val user = User(
                        uid = uId,
                        userName =  registerUserName,
                        userEmail = registerEmail,
                        password = registerPass)
                    saveToFireBaseDb(user)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "User resgistration failed", Toast.LENGTH_SHORT).show()
                binding.registerProgress.visibility = View.GONE
            }
    }

    private fun saveToFireBaseDb(user: User) {
        firestore.collection(Constants.UsersPath)
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Intent(context, MainActivity::class.java).apply {
                    Toast.makeText(context,"User Logged In Successful", Toast.LENGTH_SHORT).show()
                    startActivity(this)
                    activity?.finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "User Registration failed", Toast.LENGTH_SHORT).show()
            }
    }

}