package ge.finalproject.messengerapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.presenter.ChatListPresenter
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

        ChatListPresenter(this).loadChatHeaders()
    }

    override fun onChatHeadersLoaded(chatHeaders: ArrayList<ChatHeader>) {
        chatListView = ChatListAdapter(chatHeaders)
        chatListView.setOnItemClickListener(object : ChatListAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                startActivity(Intent(myView.context, ChatActivity::class.java).apply {
                    putExtra(ChatActivity.CHAT_ID, chatHeaders[position].chatId)
                    putExtra(ChatActivity.NICKNAME, chatHeaders[position].nickname)
                    putExtra(ChatActivity.PROFILE_PIC, chatHeaders[position].profilePic)
                    putExtra(ChatActivity.JOB, chatHeaders[position].job)
                })

            }
        })

        myView.findViewById<RecyclerView>(R.id.chatHeaderList).adapter = chatListView
    }

    override fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader) {
        chatListView.onChatHeaderUpdated(chatId, chatHeader)
    }
}