package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.FloatingSearchView
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.presenter.UserPresenter
import ge.finalproject.messengerapp.view.IUserListView
import ge.finalproject.messengerapp.view.UserListAdapter
import java.util.*


class UserSearchActivity : AppCompatActivity(), IUserListView, UserHeaderListListener {
    private lateinit var recyclerView: RecyclerView
    private var adapter = UserListAdapter(this)
    private lateinit var presenter: UserPresenter
    private lateinit var searchBar: FloatingSearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)
        recyclerView = findViewById(R.id.userHeaderList)
        searchBar = findViewById(R.id.floating_search_view)
        presenter = UserPresenter(this)
        presenter.loadUserHeaders()
        searchBar.setOnQueryChangeListener(object : FloatingSearchView.OnQueryChangeListener {
            override fun onSearchTextChanged(oldQuery: String?, newQuery: String?) {
                if (newQuery.toString().length >= 3 || newQuery.toString().isEmpty()){
                    presenter.search(newQuery.toString())
                }
            }
        })
    }

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context, UserSearchActivity::class.java))
        }
    }

    override fun onUserHeadersLoaded(userHeaders: ArrayList<UserHeader?>) {
        adapter.numLoaded = 0
        adapter.list = userHeaders
        Log.d("LIST", adapter.list.toString())
        recyclerView.adapter = adapter
    }

    override fun onChatIdLoaded(chatId: String, userHeader: UserHeader?) {
        var intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.CHAT_ID, chatId)
        intent.putExtra(ChatActivity.NICKNAME, userHeader?.nickname)
        intent.putExtra(ChatActivity.JOB, userHeader?.job)
        intent.putExtra(ChatActivity.PROFILE_PIC, userHeader?.profilePicture)
        startActivity(intent)
    }

    override fun onUserHeaderClicked(userHeader: UserHeader) {
        presenter.getChatId(userHeader.nickname)
    }
}

interface UserHeaderListListener{
    fun onUserHeaderClicked(userHeader: UserHeader)
}

