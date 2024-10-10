package com.rashid.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.rashid.chatapp.databinding.GroupItemBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.ChatModel

class GroupListAdapter(
    val mContext : Context,
    options: FirestoreRecyclerOptions<ChatModel.GroupChatRoom>
) :
    FirestoreRecyclerAdapter<ChatModel.GroupChatRoom, GroupListAdapter.ViewHolder>(
        options
    ) {

    inner class ViewHolder(val binding : GroupItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GroupItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatModel.GroupChatRoom) {
        holder.binding.groupNameTv.text = model.groupName
        for (user in model.userIds){
            FirebaseFirestore.getInstance()
                .collection(Constants.UsersPath)
                .document(user)
                .get()
                .addOnSuccessListener {
                    holder.binding.groupMembersTv.text =
                        if (holder.binding.groupMembersTv.text.isBlank()
                            || holder.binding.groupMembersTv.text.isEmpty()
                            ){
                             it.getString("userName")
                        }else{
                            holder.binding.groupMembersTv.text.toString() +
                                    "," +  it.getString("userName")
                        }

                }
        }

        holder.binding.joinGroup.text =
            if (model.userIds.contains(FirebaseUtil.currentUserId)){
                "Joined"
            }else{
                "Join"
            }

        holder.binding.joinGroup.setOnClickListener {
            if (holder.binding.joinGroup.text.toString() == "Join"){
                val userlist = model.userIds  as ArrayList
                userlist.add(FirebaseUtil.currentUserId!!)
                FirebaseFirestore.getInstance()
                    .collection(Constants.GroupsPath)
                    .document(model.groupId)
                    .update("userIds", userlist)
                    .addOnSuccessListener {
                        holder.binding.joinGroup.text = "Joined"
                        Toast.makeText(mContext, "Joined Successfully", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(mContext, "Already Joined", Toast.LENGTH_SHORT).show()
            }
        }

    }
}