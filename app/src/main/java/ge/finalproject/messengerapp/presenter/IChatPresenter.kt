package ge.finalproject.messengerapp.presenter

interface IChatPresenter {

    fun initListener(chatId: String)

    fun loadChat(chatId: String, maxTime: Long, limit: Int, startIndex: Int)

    fun sendMessage(text: String)

}