package com.rashid.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.rashid.chatapp.databinding.GroupItemBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.model.GroupChatRoom

class GroupListAdapter(
    options: FirestoreRecyclerOptions<GroupChatRoom>
) :
    FirestoreRecyclerAdapter<GroupChatRoom, GroupListAdapter.ViewHolder>(
        options
    ) {

    inner class ViewHolder(val binding : GroupItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GroupItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: GroupChatRoom) {
        holder.binding.groupNameTv.text = model.groupName
        for (user in model.userIds){
            FirebaseFirestore.getInstance()
                .collection(Constants.UsersPath)
                .document(user)
                .get()
                .addOnSuccessListener {
                    holder.binding.groupMembersTv.text =
                        holder.binding.groupMembersTv.text.toString() +
                                "," + it.getString("userName")
                }
        }

    }
}