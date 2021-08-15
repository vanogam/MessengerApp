package ge.finalproject.messengerapp.presenter

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.view.IChatListView
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class ChatPresenter(val view: IChatListView): IChatPresenter {

    lateinit var contactListener: ValueEventListener

    private val chatHeaderListeners = HashMap<String, ValueEventListener>()

    lateinit var cachedContacts: HashMap<String, Any>

    val cachedChatHeaders = ConcurrentHashMap<String, ChatHeader?>()

    val numLoaded = AtomicInteger(0)


    override fun loadChatHeaders() {
        getContacts()
    }

    override fun onChatHeadersLoaded() {
        var chatHeaderList = ArrayList<ChatHeader>()

        for (key in cachedChatHeaders.keys) {
            if (cachedChatHeaders[key] != null) {
                cachedChatHeaders[key]!!.chatId = key
                chatHeaderList.add(cachedChatHeaders[key]!!)
            }
        }
        chatHeaderList.sort()
        chatHeaderList.reverse()
        view.onChatHeadersLoaded(chatHeaderList)
    }

    override fun onChatHeaderUpdated() {
        TODO("Not yet implemented")
    }

    override fun onChatHeaderAdded() {
        TODO("Not yet implemented")
    }


    fun getContacts() {
        if (!this::contactListener.isInitialized) {
            contactListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    cachedContacts = dataSnapshot.value as HashMap<String, Any>
                    onContactsLoaded()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", "error ar getContacts: ${error.message}")
                }
            }

            Firebase.database.reference
                    .child("contacts")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(contactListener)
        }

        if (this::cachedContacts.isInitialized) {
            onContactsLoaded()
        }
    }

    fun getChatHeader(chatId: String) {
        if (!chatHeaderListeners.containsKey(chatId)) {
            chatHeaderListeners[chatId] = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    cachedChatHeaders[chatId] = dataSnapshot.getValue(ChatHeader::class.java)
                    getUserInfo(chatId)
                }

                override fun onCancelled(error: DatabaseError) {
                    cachedChatHeaders[chatId] = null
                    Log.d("ERROR", "error ar getChatHeader: ${error.message}")
                    getUserInfo(chatId)
                }
            }

            Firebase.database.reference
                .child("chatInfos")
                .child(chatId)
                .addValueEventListener(chatHeaderListeners[chatId]!!)
        }

        if (cachedChatHeaders.containsKey(chatId)) {
            onChatHeaderLoaded()
        }
    }

    fun getChatHeaders() {
        for (value in cachedContacts.values) {
            getChatHeader(value as String)
        }

    }

    fun onContactsLoaded() {
        getChatHeaders()
    }

    fun onChatHeaderLoaded() {
        var loaded = numLoaded.incrementAndGet()
        if (loaded == cachedContacts.size) {
            onChatHeadersLoaded()
        }
    }

    fun getUserInfo(chatId: String) {
        var chatHeader = cachedChatHeaders[chatId]
        var uid: String
        if (chatHeader!!.firstUser == FirebaseAuth.getInstance().currentUser!!.uid) {
            uid = chatHeader.secondUser
        } else {
            uid = chatHeader.firstUser
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u = snapshot.getValue(UserHeader::class.java)
                Log.d("UUU", u!!.profilePicture)
                chatHeader.nickname = u!!.nickname!!
                chatHeader.profilePic = u.profilePicture
                onChatHeaderLoaded()
                Firebase.database.reference
                    .child("users")
                    .child(uid)
                    .removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "Error at getUserInfo: ${error.message}")
                Firebase.database.reference
                    .child("users")
                    .child(uid)
                    .removeEventListener(this)
            }

        }
        Firebase.database.reference
            .child("users")
            .child(uid)
            .addValueEventListener(listener)

    }
}