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
import com.rashid.chatapp.databinding.ActivityChatBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatMessage
import com.rashid.chatapp.model.ChatRoom
import com.rashid.chatapp.model.User


class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var chatMessageAdapter: ChatMessageAdapter

    var otherUser : User? = null
    var chatRoomModel : ChatRoom? = null
    var chatRoomId : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding =  ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        setupUI()
        handleClickListener()
        setupRV()
    }

    private fun setupRV() {
        val query: Query = firestore.collection(Constants.ChatRoomPath)
            .document(chatRoomId)
            .collection(Constants.ChatsPath)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<ChatMessage> =
            FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage::class.java).build()

        chatMessageAdapter = ChatMessageAdapter(options)

        chatMessageAdapter.startListening()
        chatMessageAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRv.smoothScrollToPosition(0)
            }
        })

        binding.chatRv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)
        binding.chatRv.adapter = chatMessageAdapter

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
        chatRoomModel?.lastMessage = message
        chatRoomModel?.lastMessageSenderId = FirebaseUtil.currentUserId!!

        firestore.collection(Constants.ChatRoomPath)
            .document(chatRoomId)
            .set(chatRoomModel!!)

        val chatMessage = ChatMessage(message,FirebaseUtil.currentUserId, Timestamp.now())
        firestore
            .collection(Constants.ChatRoomPath)
            .document(chatRoomId)
            .collection(Constants.ChatsPath)
            .add(chatMessage)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    binding.messageEt.text.clear()
                }
            }
    }


    private fun setupUI() {
        val userString = intent.getStringExtra(Constants.userModel) ?: return
        otherUser = Gson().fromJson(userString, User::class.java)
        binding.otherUserNameTv.text = otherUser?.userName
        chatRoomId = FirebaseUtil.getUniqueChatRoomId(FirebaseUtil.currentUserId!!,otherUser?.uid!!)
        getOrCreateChatRoom()
    }

    private fun getOrCreateChatRoom(){
        firestore.collection(Constants.ChatRoomPath)
            .document()
            .get()
            .addOnCompleteListener { task ->
                chatRoomModel = task.result.toObject(ChatRoom::class.java)
                if (chatRoomModel == null){
                    chatRoomModel = ChatRoom(
                        chatRoomId = chatRoomId,
                        userIds = listOf(FirebaseUtil.currentUserId!!,otherUser?.uid!!),
                        timestamp = Timestamp.now(),
                        lastMessage = "",
                        lastMessageSenderId = ""
                    )
                    firestore.collection(Constants.ChatRoomPath)
                        .document(chatRoomId)
                        .set(chatRoomModel!!)
                }
            }
    }


}