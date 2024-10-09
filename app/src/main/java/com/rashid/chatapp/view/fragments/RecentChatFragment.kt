package com.rashid.chatapp.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.rashid.chatapp.adapters.RecentChatAdapter
import com.rashid.chatapp.databinding.FragmentRecentChatBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatRoom
import com.rashid.chatapp.view.activities.ChatActivity


class RecentChatFragment : Fragment() {

    private lateinit var binding : FragmentRecentChatBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recentChatAdapter: RecentChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
    }

    private fun setupRV() {
        val query = firestore.collection(Constants.ChatRoomPath)
            .whereArrayContains("userIds",FirebaseUtil.currentUserId!!)

        val options: FirestoreRecyclerOptions<ChatRoom> =
            FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(query, ChatRoom::class.java).build()

        recentChatAdapter = RecentChatAdapter(requireContext(),options){ user ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            val userString = Gson().toJson(user)
            intent.putExtra(Constants.userModel, userString)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.recentChatsRv.layoutManager = LinearLayoutManager(context)
        binding.recentChatsRv.adapter = recentChatAdapter
        recentChatAdapter.startListening()
    }

}