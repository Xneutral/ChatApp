package com.rashid.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.rashid.chatapp.databinding.AllUserViewBinding
import com.rashid.chatapp.databinding.ChatMessageViewBinding
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatMessage

class ChatMessageAdapter(
    options : FirestoreRecyclerOptions<ChatMessage>
) : FirestoreRecyclerAdapter<ChatMessage,ChatMessageAdapter.ViewHolder>(
    options
) {

    inner class ViewHolder(val binding : ChatMessageViewBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatMessageViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatMessage) {
        if(model.senderId == FirebaseUtil.currentUserId){
            holder.binding.leftMessageLl.visibility = View.GONE;
            holder.binding.rightMessageLl.visibility = View.VISIBLE;
            holder.binding.rightChatTv.text = model.message;
        }else{
            holder.binding.rightMessageLl.visibility = View.GONE;
            holder.binding.leftMessageLl.visibility = View.VISIBLE;
            holder.binding.leftChatTv.text = model.message;
        }
    }

}