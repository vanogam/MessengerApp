package ge.finalproject.messengerapp.view

import ge.finalproject.messengerapp.models.UserHeader

interface IUserListView {

    fun onUserHeadersLoaded(userHeaders: ArrayList<UserHeader?>)
    fun onChatIdLoaded(chatId: String, userHeader: UserHeader?)
}