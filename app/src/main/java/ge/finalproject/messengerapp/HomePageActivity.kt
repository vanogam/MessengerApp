package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.User
import ge.finalproject.messengerapp.presenter.ChatListPresenter
import ge.finalproject.messengerapp.view.ChatListAdapter
import ge.finalproject.messengerapp.view.IChatListView

class HomePageActivity : AppCompatActivity(), IChatListView {
    lateinit var chatListView: ChatListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        ChatListPresenter(this).loadChatHeaders()

    }

    companion object{

        fun startFromAuthorization(context: Context){
            context.startActivity(Intent(context, HomePageActivity::class.java))
        }

    }

    override fun onChatHeadersLoaded(chatHeaders: List<ChatHeader>) {
        chatListView = ChatListAdapter(chatHeaders)
        findViewById<RecyclerView>(R.id.chatHeaderList).adapter = chatListView
    }

    override fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader) {
        TODO("Not yet implemented")
    }

    override fun onUserLoaded(uid: String, user: User) {
        TODO("Not yet implemented")
    }
}