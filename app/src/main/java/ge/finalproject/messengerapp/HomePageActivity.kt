package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.User
import ge.finalproject.messengerapp.presenter.ChatPresenter
import ge.finalproject.messengerapp.view.ChatListAdapter
import ge.finalproject.messengerapp.view.IChatListView

class HomePageActivity : AppCompatActivity(), IChatListView {
    lateinit var chatListView: ChatListAdapter
    lateinit var btnGotoProfile: ImageButton
    lateinit var btnHomePage: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        ChatPresenter(this).loadChatHeaders()
        initView()
        btnGotoProfile.setOnClickListener{
            ProfileActivity.start(this)
        }

        btnHomePage.setOnClickListener{
            startFromAuthorization(this)
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