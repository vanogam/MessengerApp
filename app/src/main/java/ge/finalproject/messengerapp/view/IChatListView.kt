package ge.finalproject.messengerapp.view

import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.User

interface IChatListView {
    fun onChatHeadersLoaded(chatHeaders: ArrayList<ChatHeader>);

    fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader);

    fun onUserLoaded(uid: String, user: User)
}