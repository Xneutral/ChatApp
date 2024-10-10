package com.rashid.chatapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.rashid.chatapp.databinding.AllUserViewBinding
import com.rashid.chatapp.databinding.ChatMessageViewBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatMessage

class ChatMessageAdapter(
    options : FirestoreRecyclerOptions<ChatMessage>,
    val isGroupChat : Boolean
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
            holder.binding.leftMessageLl.visibility = View.GONE
            holder.binding.rightMessageLl.visibility = View.VISIBLE
            holder.binding.rightChatTv.text = model.message
        }else{
            holder.binding.rightMessageLl.visibility = View.GONE
            holder.binding.leftMessageLl.visibility = View.VISIBLE
            holder.binding.leftChatTv.text = model.message
        }

        if(model.senderId == FirebaseUtil.currentUserId && isGroupChat){
            holder.binding.rightChatUserNameTv.text = "You"
        }else if (isGroupChat){
            FirebaseFirestore.getInstance()
                .collection(Constants.UsersPath)
                .document(model.senderId)
                .get()
                .addOnSuccessListener {
                    holder.binding.leftChatUserNameTv.text  = it.getString("userName")
                }
        }else{
            holder.binding.leftChatUserNameTv.visibility = View.GONE
            holder.binding.rightChatUserNameTv.visibility = View.GONE
        }
        Log.d("Testing", "onBindViewHolder: model sender id is ${model.senderId} ")
//        if (isGroupChat){
//            holder.binding.rightChatUserNameTv.visibility = View.VISIBLE
//            holder.binding.leftChatUserNameTv.visibility = View.VISIBLE
//                Log.d("Testing", "onBindViewHolder: model sender id is ${model.senderId} ")
//                holder.binding.rightChatUserNameTv.visibility = View.VISIBLE
//                FirebaseFirestore.getInstance()
//                    .collection(Constants.UsersPath)
//                    .document(model.senderId)
//                    .get()
//                    .addOnSuccessListener { documentSnapShot ->
//                        if (documentSnapShot.exists()){
//                            holder.binding.rightChatUserNameTv.text =
//                                documentSnapShot.get("userName").toString()
//                        }
//                    }
//            }
//        else{
//                holder.binding.rightChatUserNameTv.visibility = View.GONE
//                holder.binding.leftChatUserNameTv.visibility = View.GONE
//            }

    }

}