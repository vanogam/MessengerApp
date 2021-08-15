package ge.finalproject.messengerapp.view

import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader

interface IChatListView {
    fun onChatHeadersLoaded(chatHeaders: List<ChatHeader>);

    fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader);

    fun onUserLoaded(uid: String, user: UserHeader)
}