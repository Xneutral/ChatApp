package com.rashid.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rashid.chatapp.databinding.UserSelectionItemBinding
import com.rashid.chatapp.model.User
import java.util.ArrayList

class UserSelectionAdapter (
    private val userList : List<User>
): RecyclerView.Adapter<UserSelectionAdapter.ViewHolder>() {

    private val selectedUser = ArrayList<String>(userList.size)

    inner class ViewHolder(val binding : UserSelectionItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserSelectionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user  = userList[position]
        holder.binding.userNameTv.text = user.userName
        holder.binding.userEmailTv.text = user.userEmail
        holder.binding.isUserSelected.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                selectedUser.add(position, user.uid)
            }else{
                selectedUser.remove(user.uid)
            }
        }

        holder.binding.root.setOnClickListener {
            holder.binding.isUserSelected.isChecked = !holder.binding.isUserSelected.isChecked
        }
    }

    fun getSelectedUser(): ArrayList<String>{
        return selectedUser
    }

    fun resetState() {
        selectedUser.clear()
    }
}