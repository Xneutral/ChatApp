package com.rashid.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.rashid.chatapp.R
import com.rashid.chatapp.databinding.AllUserViewBinding
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.User

class AllUserAdapter(
    val mContext : Context,
    options: FirestoreRecyclerOptions<User>,
     val onUserClick : (user : User) -> Unit
) : FirestoreRecyclerAdapter<User, AllUserAdapter.ViewHolder>(options) {

    inner class ViewHolder(val binding : AllUserViewBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AllUserViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        if (model.uid == FirebaseUtil.currentUserId){
            holder.binding.userNameTv.text = model.userName +
                    mContext.getString(R.string.me_user)
            holder.binding.root.isClickable = false
        }else{
            holder.binding.userNameTv.text = model.userName
            holder.binding.root.isClickable = true
        }
        holder.binding.userEmailTv.text = model.userEmail

        holder.binding.root.setOnClickListener {
            if (model.uid == FirebaseUtil.currentUserId) {
                return@setOnClickListener
            }
            onUserClick(model)
        }

        Glide.with(mContext)
            .load(R.drawable.user_icon)
            .circleCrop()
            .into(holder.binding.userProfileIv)



    }
}