package com.rashid.chatapp.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.rashid.chatapp.R
import com.rashid.chatapp.databinding.ActivityMainBinding
import com.rashid.chatapp.view.fragments.AllUserFragment
import com.rashid.chatapp.view.fragments.GroupListFragment
import com.rashid.chatapp.view.fragments.LoginFragment
import com.rashid.chatapp.view.fragments.NewGroupFragment
import com.rashid.chatapp.view.fragments.RecentChatFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val userFragment = AllUserFragment()
    private val recentChatFragmet = RecentChatFragment()
    private val groupListFragment = GroupListFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupFragments(recentChatFragmet)
        handleClickListener()
    }

    private fun setupFragments(fragment : Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .disallowAddToBackStack()
            .commit()
    }


    private fun handleClickListener(){
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.user_list ->{
                    setupFragments(userFragment)
                    true
                }
                R.id.user_chat_room -> {
                    setupFragments(recentChatFragmet)
                    true
                }
                R.id.group_list ->{
                    setupFragments(groupListFragment)
                    true
                }

                else -> true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseAuth.getInstance().signOut()
    }
}