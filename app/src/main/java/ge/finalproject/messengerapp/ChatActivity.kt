package ge.finalproject.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import ge.finalproject.messengerapp.models.Message
import ge.finalproject.messengerapp.presenter.ChatPresenter
import ge.finalproject.messengerapp.presenter.IChatPresenter
import ge.finalproject.messengerapp.view.IChatView

class ChatActivity : AppCompatActivity(), IChatView {

    private lateinit var avatar: ImageView
    private lateinit var appBar:AppBarLayout
    private lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var backButton: ImageButton
    private lateinit var name:TextView
    private lateinit var subtitle:TextView
    private lateinit var messages:RecyclerView
    private lateinit var chatId: String
    private lateinit var presenter: IChatPresenter
    private lateinit var adapter: ChatAdapter

    private var initialHeight = 0

    var toolbarHeight = 0
    var scale: Float = 0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        avatar = findViewById(R.id.chatAvatar)
        appBar = findViewById(R.id.appBarLayout)
        toolbar = findViewById(R.id.toolbar)
        name = findViewById(R.id.contactName)
        subtitle = findViewById(R.id.subtitle)
        backButton = findViewById(R.id.backButton)
        messages = findViewById(R.id.messagesList)

        initialHeight = avatar.layoutParams.height
        toolbarHeight = toolbar.layoutParams.height
        scale = resources.displayMetrics.density

        chatId = intent.getStringExtra(CHAT_ID)!!
        name.text = intent.getStringExtra(NICKNAME)!!
        subtitle.text = intent.getStringExtra(JOB)!!
        chatId = intent.getStringExtra(CHAT_ID)!!

        presenter = ChatPresenter(this)
        adapter = ChatAdapter(this, chatId, messages)
        presenter.initListener(chatId)

        messages.adapter = adapter
        messages.scrollToPosition(15)

        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                applyOffset(Math.abs(verticalOffset / appBar.totalScrollRange.toFloat()))
            }

        })
    }

    fun applyOffset(offset: Float) {
        val shrinkHeight = (initialHeight - (48 * scale + 0.5f))
        avatar.translationX = (offset* (30).toFloat() )
        avatar.translationY = (offset* (24 * scale) )
        avatar.layoutParams.height = (initialHeight - (offset * shrinkHeight )).toInt()
        avatar.layoutParams.width = (initialHeight - (offset * shrinkHeight )).toInt()

        backButton.translationY =  (96) * offset * scale
        backButton.translationX =  (-5) * offset * scale

        name.translationX =  (32) * offset * scale
        name.translationY =  (44) * offset * scale

        subtitle.translationX =  (32) * offset * scale
        subtitle.translationY =  (18) * offset * scale

        avatar.post {
            avatar.requestLayout();
        }
    }

    override fun onMessageLoaded(id: Int, message: Message) {
        adapter.onMessageLoaded(id, message)
    }

    override fun onMessageAdded(message: Message) {
        adapter.onMessageAdded(message)
    }

    override fun loadMessages(id: String, fromTime: Long, limit: Int, startIndex: Int) {
        presenter.loadChat(id, fromTime, limit, startIndex)
    }

    override fun getViewScale(): Float {
        return scale
    }

    companion object {
        const val CHAT_ID = "CHAT_ID"
        const val NICKNAME = "NICKNAME"
        const val JOB = "JOB"
        const val PROFILE_PIC = "PROFILE_PIC"
    }
}