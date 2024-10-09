package com.rashid.chatapp.helper

import com.google.firebase.auth.FirebaseAuth

object FirebaseUtil {

    val  currentUserId = FirebaseAuth.getInstance().currentUser?.uid


    fun getUniqueChatRoomId (userId1 : String, userId2: String): String{
        return if (userId1.hashCode()< userId2.hashCode()){
            userId1 + "_" + userId2
        }else{
            userId2 + "_" + userId1
        }
    }

}