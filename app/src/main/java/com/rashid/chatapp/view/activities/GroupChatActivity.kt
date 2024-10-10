package com.rashid.chatapp.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.rashid.chatapp.R
import com.rashid.chatapp.adapters.ChatMessageAdapter
import com.rashid.chatapp.databinding.ActivityGroupChatBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatMessage
import com.rashid.chatapp.model.ChatModel
import com.rashid.chatapp.model.User

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGroupChatBinding

    lateinit var firestore: FirebaseFirestore
    private lateinit var chatMessageAdapter: ChatMessageAdapter

    var groupChatModel : ChatModel.GroupChatRoom? = null
    private var groupId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        setupUI()
        handleClickListener()
    }

    private fun setupRV() {
        val query: Query = firestore.collection(Constants.GroupsPath)
            .document(groupId)
            .collection(Constants.ChatsPath)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<ChatMessage> =
            FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage::class.java).build()

        chatMessageAdapter = ChatMessageAdapter(options,true)

        chatMessageAdapter.startListening()
        chatMessageAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.groupChatRv.smoothScrollToPosition(0)
            }
        })

        binding.groupChatRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true)
        binding.groupChatRv.adapter = chatMessageAdapter

        chatMessageAdapter.startListening()
    }

    private fun handleClickListener() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.sendMsgBtn.setOnClickListener {
            if(binding.messageEt.text.isNotBlank() && binding.messageEt.text.isNotEmpty()){
                sendMessage(binding.messageEt.text.toString())
            }
        }
    }

    private fun sendMessage(message: String) {
        groupChatModel?.lastMessage = message
        groupChatModel?.lastMessageSenderId = FirebaseUtil.currentUserId!!
        groupChatModel?.timestamp = Timestamp.now()

        firestore.collection(Constants.GroupsPath)
            .document(groupId)
            .set(groupChatModel!!)

        val chatMessage = ChatMessage(message, FirebaseUtil.currentUserId, Timestamp.now())
        firestore
            .collection(Constants.GroupsPath)
            .document(groupId)
            .collection(Constants.ChatsPath)
            .add(chatMessage)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    binding.messageEt.text.clear()
                }
            }
    }

    private fun setupUI() {
        groupId =  intent.getStringExtra(Constants.groupId)?: return
        firestore.collection(Constants.GroupsPath)
            .document(groupId)
            .get()
            .addOnCompleteListener { task ->
                groupChatModel = task.result.toObject(ChatModel.GroupChatRoom::class.java)
                firestore.collection(Constants.GroupsPath)
                    .document(groupId)
                    .set(groupChatModel!!)

                binding.groupNameTv.text = groupChatModel?.groupName
                setupRV()
            }
    }
}