package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.presenter.ChatListPresenter
import ge.finalproject.messengerapp.view.ChatListAdapter
import ge.finalproject.messengerapp.view.IChatListView

class HomePageActivity : AppCompatActivity(), IChatListView {
    lateinit var chatListView: ChatListAdapter
    lateinit var btnGotoProfile: ImageButton
    lateinit var btnHomePage: ImageButton
    lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        ChatListPresenter(this).loadChatHeaders()
        initView()
        btnGotoProfile.setOnClickListener{
            ProfileActivity.start(this)
        }

        btnHomePage.setOnClickListener{
            startFromAuthorization(this)
        }

        fab.setOnClickListener{
            UserSearchActivity.start(this)
        }
    }

    companion object{
        fun startFromAuthorization(context: Context){
            context.startActivity(Intent(context, HomePageActivity::class.java))
        }
    }
    private fun initView(){
        btnGotoProfile = findViewById(R.id.goto_profile_btn)
        btnHomePage = findViewById(R.id.home_page_btn)
        fab = findViewById(R.id.fab)
    }


    override fun onChatHeadersLoaded(chatHeaders: ArrayList<ChatHeader>) {
        chatListView = ChatListAdapter(chatHeaders)
        findViewById<RecyclerView>(R.id.chatHeaderList).adapter = chatListView
    }

    override fun onChatHeaderUpdated(chatId: String, chatHeader: ChatHeader) {
        chatListView.onChatHeaderUpdated(chatId, chatHeader)
    }

    override fun onUserLoaded(uid: String, user: UserHeader) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        var intent = Intent(ACTION_MAIN)
        intent.addCategory(CATEGORY_HOME)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}