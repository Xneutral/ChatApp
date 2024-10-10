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
import com.rashid.chatapp.model.ChatModel
import com.rashid.chatapp.view.activities.ChatActivity
import com.rashid.chatapp.view.activities.GroupChatActivity


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

//        val options: FirestoreRecyclerOptions<ChatModel.ChatRoom> =
//            FirestoreRecyclerOptions.Builder<ChatModel.ChatRoom>()
//                .setQuery(query, ChatModel.ChatRoom::class.java).build()
        val query_one = firestore.collection(Constants.GroupsPath)
            .whereArrayContains("userIds", FirebaseUtil.currentUserId)

        query.get().addOnSuccessListener { collectionOneSnapshot  ->
            if (!isAdded) return@addOnSuccessListener
            query_one.get().addOnSuccessListener { collectionTwoSnapshot ->
                if (!isAdded) return@addOnSuccessListener
                val mergedData = mutableListOf<ChatModel>()
                mergedData.addAll(collectionOneSnapshot.toObjects(ChatModel.ChatRoom::class.java))
                mergedData.addAll(collectionTwoSnapshot.toObjects(ChatModel.GroupChatRoom::class.java))

                recentChatAdapter = RecentChatAdapter(requireContext(),mergedData){ user, isGroupChat, groupId ->
                    if (!isGroupChat){
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        val userString = Gson().toJson(user)
                        intent.putExtra(Constants.userModel, userString)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }else{
                        val intent = Intent(requireContext(), GroupChatActivity::class.java)
                        intent.putExtra(Constants.groupId, groupId)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                binding.recentChatsRv.layoutManager = LinearLayoutManager(context)
                binding.recentChatsRv.adapter = recentChatAdapter
            }
        }

//        recentChatAdapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        setupRV()
    }

}