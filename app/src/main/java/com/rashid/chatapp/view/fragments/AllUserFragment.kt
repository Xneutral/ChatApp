package com.rashid.chatapp.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.rashid.chatapp.R
import com.rashid.chatapp.adapters.AllUserAdapter
import com.rashid.chatapp.databinding.FragmentAllUserBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.model.User
import com.rashid.chatapp.view.activities.ChatActivity

class AllUserFragment : Fragment() {

    private lateinit var binding : FragmentAllUserBinding

    private lateinit var userAdapter: AllUserAdapter

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentAllUserBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
    }

    private fun setupRV() {
        val query: Query = FirebaseFirestore.getInstance()
            .collection(Constants.UsersPath)



        val options: FirestoreRecyclerOptions<User> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()


        userAdapter = AllUserAdapter(requireContext(), options){ user ->
            val intent = Intent(context, ChatActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val userString = Gson().toJson(user)
            intent.putExtra(Constants.userModel, userString)
            startActivity(intent)
            Log.d("TAG", "click on user: $user ")
        }


        binding.usersRv.layoutManager = LinearLayoutManager(context)
        binding.usersRv.adapter = userAdapter
        userAdapter.startListening()
    }


    override fun onDestroy() {
        super.onDestroy()
        userAdapter.stopListening()
    }

}