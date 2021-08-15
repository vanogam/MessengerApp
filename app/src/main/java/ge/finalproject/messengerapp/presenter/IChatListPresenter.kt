package ge.finalproject.messengerapp.presenter


interface IChatListPresenter {

    fun loadChatHeaders()

    fun onChatHeadersLoaded()

    fun onChatHeaderUpdated(chatId: String)

    fun onChatHeaderAdded()

}