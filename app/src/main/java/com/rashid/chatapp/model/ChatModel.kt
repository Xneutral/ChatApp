package com.rashid.chatapp.model

import com.google.firebase.Timestamp

sealed class ChatModel{

    data class ChatRoom(
        val chatRoomId : String = "",
        val userIds : List<String>  = emptyList(),
        val timestamp: Timestamp = Timestamp.now(),
        var lastMessageSenderId : String = "",
        var lastMessage : String = ""
    ) : ChatModel()

    data class GroupChatRoom(
        val groupId : String ="",
        val groupName : String ="",
        var userIds : List<String>  = emptyList(),
        var timestamp: Timestamp = Timestamp.now(),
        var lastMessageSenderId : String = "",
        var lastMessage : String = ""
    ) : ChatModel()

}