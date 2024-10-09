package com.rashid.chatapp.model

import com.google.firebase.Timestamp

data class GroupChatRoom(
    val groupId : String ="",
    val groupName : String ="",
    var userIds : List<String>  = emptyList(),
    val timestamp: Timestamp = Timestamp.now(),
    var lastMessageSenderId : String = "",
    var lastMessage : String = ""
)