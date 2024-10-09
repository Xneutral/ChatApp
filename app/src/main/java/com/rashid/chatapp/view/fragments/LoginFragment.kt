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
import com.rashid.chatapp.R
import com.rashid.chatapp.databinding.FragmentLoginBinding
import com.rashid.chatapp.view.activities.MainActivity

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClickListener()
    }

    private fun handleClickListener() {

        binding.loginBtn.setOnClickListener {
            checkCredentials()
        }

        binding.registerTv.setOnClickListener {
            findNavController()
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun checkCredentials() {
        val loginEmail = binding.loginEmail.editText?.text.toString()
        val loginPass = binding.loginPassword.editText?.text.toString()
        if (loginPass.isBlank() || loginPass.isEmpty()
            || loginEmail.isBlank() || loginEmail.isEmpty()
        ){
            Toast.makeText(context,"Provide All Credentials", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Intent(context, MainActivity::class.java).apply {
                        startActivity(this)
                        activity?.finish()
                    }
                    Toast.makeText(context,"Log In Successful", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,"Log In Failed", Toast.LENGTH_SHORT).show()
            }
    }

}