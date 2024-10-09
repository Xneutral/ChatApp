package com.rashid.chatapp.model

import com.google.firebase.Timestamp

data class ChatRoom(
    val chatRoomId : String = "",
    val userIds : List<String>  = emptyList(),
    val timestamp: Timestamp = Timestamp.now(),
    var lastMessageSenderId : String = "",
    var lastMessage : String = ""
)
