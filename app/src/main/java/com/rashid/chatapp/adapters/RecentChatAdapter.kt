package com.rashid.chatapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.rashid.chatapp.R
import com.rashid.chatapp.databinding.RecentChatViewBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatMessage
import com.rashid.chatapp.model.ChatModel
import com.rashid.chatapp.model.User
import java.text.SimpleDateFormat
import java.util.Locale

class RecentChatAdapter(
    private val mContext: Context,
    private val chatList : List<ChatModel>,
//    options: FirestoreRecyclerOptions<ChatModel>,
    val onChatClick:(user : User?, isGroupChat : Boolean, groupId : String?) -> Unit
) : RecyclerView.Adapter<RecentChatAdapter.ViewHolder>()
//    : FirestoreRecyclerAdapter<ChatModel, RecentChatAdapter.ViewHolder>(
//    options
//)
{

    inner class ViewHolder(val binding: RecentChatViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecentChatViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun getItemCount() = chatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(val model = chatList[position]){
            is ChatModel.ChatRoom -> {
                Log.d("testing", "onBindViewHolder: position $position is chatroom")
                val otherUserIds = if (model.userIds[0] == FirebaseUtil.currentUserId) {
                    model.userIds[1]
                }else{
                    model.userIds[0]
                }
                FirebaseFirestore
                    .getInstance()
                    .collection(Constants.UsersPath)
                    .document(otherUserIds)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                val otherUserModel = document.toObject(User::class.java)
                                holder.binding.userNameTv.text = otherUserModel?.userName
                            } else {
                                Log.d("FirestoreDebug", "No such document exists for ID: $otherUserIds")
                            }
                        } else {
                            Log.w("FirestoreError", "Failed to fetch document", task.exception)
                        }
                    }

                holder.binding.timestampTv.text = convertTimestamp(model.timestamp)
                holder.binding.lastMessageTv.text =
                    if (model.lastMessageSenderId == FirebaseUtil.currentUserId) {
                        mContext.getString(R.string.you) + model.lastMessage
                    } else {
                        model.lastMessage
                    }
                holder.binding.root.setOnClickListener {
                    FirebaseFirestore.getInstance()
                        .collection(Constants.UsersPath)
                        .document(otherUserIds)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    val otherUserModel = document.toObject(User::class.java)
                                    if (otherUserModel != null) {
                                        onChatClick(otherUserModel,false, null)
                                    }
                                }
                            }
                        }
                }
            }

            is ChatModel.GroupChatRoom -> {
                Log.d("testing", "onBindViewHolder: position $position is group")
                holder.binding.userNameTv.text =model.groupName
                /*FirebaseFirestore
                    .getInstance()
                    .collection(Constants.GroupsPath)
                    .document(model.group)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                val otherUserModel = document.toObject(User::class.java)

                            } else {
                                Log.d("FirestoreDebug", "No such document exists for ID: ${model.lastMessageSenderId}")
                            }
                        } else {
                            Log.w("FirestoreError", "Failed to fetch document", task.exception)
                        }
                    }*/
                holder.binding.timestampTv.text = convertTimestamp(model.timestamp)
                holder.binding.lastMessageTv.text =
                    if (model.lastMessageSenderId == FirebaseUtil.currentUserId) {
                        mContext.getString(R.string.you) + model.lastMessage
                    } else {
                        model.lastMessage
                    }
                holder.binding.root.setOnClickListener {
                    /*FirebaseFirestore.getInstance()
                        .collection(Constants.GroupsPath)
                        .document(model.lastMessageSenderId)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    val otherUserModel = document.toObject(User::class.java)
                                    if (otherUserModel != null) {
                                        // Trigger the chat click callback
                                        onChatClick(otherUserModel, true)
                                    }
                                }
                            }
                        }*/
                    onChatClick(null, true, model.groupId)
                }
            }
        }
    }


    /*override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatModel) {

        when(model){
            is ChatModel.ChatRoom -> {
                val otherUserIds = if (model.userIds[0] == FirebaseUtil.currentUserId) {
                    model.userIds[1]
                }else{
                    model.userIds[0]
                }
                FirebaseFirestore
                    .getInstance()
                    .collection(Constants.UsersPath)
                    .document(otherUserIds)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                val otherUserModel = document.toObject(User::class.java)
                                holder.binding.userNameTv.text = otherUserModel?.userName
                            } else {
                                Log.d("FirestoreDebug", "No such document exists for ID: $otherUserIds")
                            }
                        } else {
                            Log.w("FirestoreError", "Failed to fetch document", task.exception)
                        }
                    }

                holder.binding.timestampTv.text = convertTimestamp(model.timestamp)
                holder.binding.lastMessageTv.text =
                    if (model.lastMessageSenderId == FirebaseUtil.currentUserId) {
                        mContext.getString(R.string.you) + model.lastMessage
                    } else {
                        model.lastMessage
                    }
                holder.binding.root.setOnClickListener {
                    FirebaseFirestore.getInstance()
                        .collection(Constants.UsersPath)
                        .document(otherUserIds)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    val otherUserModel = document.toObject(User::class.java)
                                    if (otherUserModel != null) {
                                        // Trigger the chat click callback
                                        onChatClick(otherUserModel)
                                    }
                                }
                            }
                        }
                }
            }

            is ChatModel.GroupChatRoom -> {

            }
        }

    }*/

    private fun convertTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return sdf.format(date)
    }

}