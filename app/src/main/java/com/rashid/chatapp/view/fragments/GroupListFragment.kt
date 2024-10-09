package com.rashid.chatapp.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rashid.chatapp.R
import com.rashid.chatapp.adapters.GroupListAdapter
import com.rashid.chatapp.databinding.FragmentGroupListBinding
import com.rashid.chatapp.helper.Constants
import com.rashid.chatapp.model.GroupChatRoom
import com.rashid.chatapp.model.User


class GroupListFragment : Fragment() {

    private lateinit var binding : FragmentGroupListBinding

    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
        handleClickListener()
    }

    val newGroupFragment = NewGroupFragment()

    private fun handleClickListener() {
        binding.newGroup.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, newGroupFragment)
                ?.addToBackStack(null)
                ?.commit()
        }
    }


    private fun setupRV() {
        val query: Query = firestore
            .collection(Constants.GroupsPath)

        val options: FirestoreRecyclerOptions<GroupChatRoom> = FirestoreRecyclerOptions.Builder<GroupChatRoom>()
            .setQuery(query, GroupChatRoom::class.java)
            .build()

        binding.groupListRv.layoutManager = LinearLayoutManager(context)
        val groupListAdapter = GroupListAdapter(options)
        binding.groupListRv.adapter = groupListAdapter

        groupListAdapter.startListening()
    }

}