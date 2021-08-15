package ge.finalproject.messengerapp.models

import android.provider.ContactsContract

data class ChatHeader(
    var firstUser: String = "",
    var secondUser: String = "",
    var lastMessage: String = "",
    var job: String = "",
    var lastMessageSender: String = "",
    var lastMessageTime: Long = 0,
    var chatId: String = "",
    var nickname: String = "",
    var profilePic: String = ""
) : Comparable<ChatHeader> {

    override fun compareTo(other: ChatHeader): Int {
        return (lastMessageTime - other.lastMessageTime).toInt()
    }
};