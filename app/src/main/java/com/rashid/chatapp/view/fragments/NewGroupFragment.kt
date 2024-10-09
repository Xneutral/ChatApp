package com.rashid.chatapp.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.rashid.chatapp.R
import com.rashid.chatapp.adapters.UserSelectionAdapter
import com.rashid.chatapp.databinding.FragmentNewGroupBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.helper.FirebaseUtil
import com.rashid.chatapp.model.GroupChatRoom
import com.rashid.chatapp.model.User

class NewGroupFragment : Fragment() {

    private lateinit var binding : FragmentNewGroupBinding
    var userSelectionAdapter : UserSelectionAdapter? = null
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClickListener()
        setupRV()
    }

    private fun setupRV() {
        firestore.collection(Constants.UsersPath)
            .get()
            .addOnSuccessListener { querySnap ->
                val list = ArrayList<User>()
                for (document in querySnap.documents){
                    document.toObject(User::class.java)?.let {
                        if (it.uid == FirebaseUtil.currentUserId) return@let
                        list.add(it)
                    }
                }
                binding.memberSelectionRv.layoutManager = LinearLayoutManager(context)
                userSelectionAdapter = UserSelectionAdapter(list)
                binding.memberSelectionRv.adapter = userSelectionAdapter
            }
    }

    private fun handleClickListener() {


        binding.createGroup.setOnClickListener {
            if (binding.groupNameEt.text.isBlank()
                || binding.groupNameEt.text.isEmpty()
                || userSelectionAdapter ==  null
                || (userSelectionAdapter?.getSelectedUser()?.isEmpty() == true)
            ){
                Toast.makeText(context, "Provide All Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userList = userSelectionAdapter?.getSelectedUser()
            userList?.add(FirebaseUtil.currentUserId!!)
            val groupChatRoom = GroupChatRoom(
                groupName = binding.groupNameEt.text.toString(),
                userIds = userList!!,
                lastMessage = "",
                lastMessageSenderId = ""
            )
            firestore.collection(Constants.GroupsPath)
            .add(groupChatRoom)
            .addOnSuccessListener { documentReference ->
                val generatedGroupId = documentReference.id  // Get the generated ID
                Log.d("Firestore", "Group created with ID: $generatedGroupId")
                documentReference.update("groupId",generatedGroupId)

                initialState()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error creating group", e)
            }
        }
    }

    private fun initialState() {
        binding.groupNameEt.text.clear()
        userSelectionAdapter?.resetState()
        activity?.
        supportFragmentManager
            ?.popBackStack()
    }

}