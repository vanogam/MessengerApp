package ge.finalproject.messengerapp.view

import ge.finalproject.messengerapp.models.Message

interface IChatView {

    fun onMessageLoaded(id: Int, message: Message)

    fun onMessageAdded(message: Message)

    fun loadMessages(id: String, fromTime: Long, limit: Int, startIndex: Int)

    fun getViewScale(): Float

}