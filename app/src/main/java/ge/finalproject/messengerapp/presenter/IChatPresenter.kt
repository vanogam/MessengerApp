package ge.finalproject.messengerapp.presenter

import ge.finalproject.messengerapp.models.ChatHeader


interface IChatPresenter {

    fun loadChatHeaders()

    fun onChatHeadersLoaded()

    fun onChatHeaderUpdated()

    fun onChatHeaderAdded()

}