package ge.finalproject.messengerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.presenter.ChatPresenter
import ge.finalproject.messengerapp.view.ChatListAdapter
import ge.finalproject.messengerapp.view.IChatListView

class HomePageFragment : Fragment(), IChatListView {
    lateinit var chatListView: ChatListAdapter
    lateinit var myView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myView = view
        ChatPresenter(this).loadChatHeaders()
    }

    override fun onChatHeadersLoaded(chatHeaders: List<ChatHeader>) {
        chatListView = ChatListAdapter(chatHeaders)
        myView.findViewById<RecyclerView>(R.id.chatHeaderList).adapter = chatListView
    }

    override fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader) {
        TODO("Not yet implemented")
    }

    override fun onUserLoaded(uid: String, user: UserHeader) {
        TODO("Not yet implemented")
    }
}