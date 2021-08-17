package ge.finalproject.messengerapp.presenter

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.finalproject.messengerapp.UserSearchActivity
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.Message
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.view.IUserListView
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserPresenter(val view: IUserListView?): IUserPresenter {
    private var usersReference: DatabaseReference = Firebase.database.reference
                                                    .child("users")
    lateinit var userListener: ValueEventListener
    lateinit var users: HashMap<String, Any>
    var userHeaders = HashMap<String, UserHeader?>() //userId - UserHeader
    val numLoaded = AtomicInteger(0)
    var userHeaderList = ArrayList<UserHeader?>()

    lateinit var chatInfosList: ArrayList<ChatHeader?>
    var chatHeaders = HashMap<String, ChatHeader?>() //chatId - ChatHeader

    lateinit var chats: ArrayList<HashMap<String, Any>>
    lateinit var myContacts: HashMap<String, Any>
    lateinit var chatInfos: ArrayList<Any>
    lateinit var contacts: HashMap<String, HashMap<String, String>>

    fun getUserIdByNickname(nickname: String): String {
        for (key in userHeaders.keys){
            if (userHeaders[key]?.nickname == nickname){
                return key
            }
        }
        return ""
    }

    fun getChatId(nickname: String){
        var userId = getUserIdByNickname(nickname)
        isUserInContacts(userId)
//        getChats(userId)
    }

    fun isUserInContacts(userId: String){
        val ref = Firebase.database.reference
            .child("contacts")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                myContacts = snapshot.value as HashMap<String, Any>
                if (myContacts.containsKey(userId)){
                    getChats(userId)
                }else{
                    addUserInChats(userId)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error ar isUserInContacts: ${error.message}")
            }
        }

        ref.addValueEventListener(listener)
    }

    fun addUserInChats(userId: String){
        val ref = Firebase.database.reference
            .child("chats")

        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chats = snapshot.value as ArrayList<HashMap<String, Any>>
                var chatId = (chats.size-1).toString()
                var hm = HashMap<String, Any>(2)
                hm.set("0", Message())
                hm.set("1", object{ val sender = "sys"
                                    val time = 1 })
                chats.add(hm)
                snapshot.ref.setValue(chats)
                ref.removeEventListener(this)
                addUserInChatInfos((chatId.toInt()+1).toString(), userId)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error ar addUserInChats: ${error.message}")
            }
        }
        ref.addValueEventListener(listener)
    }

    fun addUserInChatInfos(chatId: String, userId: String){
        val ref = Firebase.database.reference
            .child("chatInfos")

        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatInfos = snapshot.value as ArrayList<Any>
                var chatHeader = object {
                    val firstUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    val lastMessage = ""
                    val lastMessageSender = ""
                    val lastMessageTime = 0
                    val secondUser = userId
                }
                chatInfos.add(chatHeader)
                ref.setValue(chatInfos)
                ref.removeEventListener(this)
                addUserInContacts(chatId, userId)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error ar addUserInChatInfos: ${error.message}")
            }
        }
        ref.addValueEventListener(listener)
    }

    fun addUserInContacts(chatId: String, userId: String) {
        val myId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = Firebase.database.reference
            .child("contacts")

        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                contacts = snapshot.value as HashMap<String, HashMap<String, String>>
                contacts[myId]?.set(userId, chatId)
                contacts[userId]?.set(myId, chatId)
                ref.setValue(contacts)
                ref.removeEventListener(this)
//                getChats(userId)
                view?.onChatIdLoaded(chatId, userHeaders[userId])
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error ar addUserInContacts: ${error.message}")
            }
        }
        ref.addValueEventListener(listener)
    }

//    fun addMeInUsersContacts(chatId: String, userId: String){
//        val myId = FirebaseAuth.getInstance().currentUser!!.uid
//        val ref = Firebase.database.reference
//            .child("contacts")
//
//        val listener = object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.hasChild(userId)){
//                    updateUserContact(chatId, userId, myId, true)
//                }else{
//                    contacts = snapshot.value as HashMap<String, Any>
//                    contacts[userId] = Any()
//                    ref.setValue(contacts)
//                    updateUserContact(chatId, userId, myId, true)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("ERROR", "error ar addUserInContacts: ${error.message}")
//            }
//        }
//        ref.addValueEventListener(listener)
//    }

//    fun updateUserContact(chatId: String, myId: String, userId: String, bool: Boolean) {
//        val ref = Firebase.database.reference
//            .child("contacts")
//            .child(myId)
//
//        val listener = object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                contacts = snapshot.value as HashMap<String, Any>
//                contacts[userId] = chatId
//                ref.setValue(contacts)
//                if (bool){
//                    getChats(userId)
//                }else{
//                    addMeInUsersContacts(chatId, userId)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("ERROR", "error ar updateUserContact: ${error.message}")
//            }
//        }
//        ref.addValueEventListener(listener)
//    }

    fun getChats(userId: String){
        var chatInfoListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatInfosList = snapshot.value as ArrayList<ChatHeader?>
                getChatHeaders(userId)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error ar getChats: ${error.message}")
            }

        }
        Firebase.database.reference
            .child("chatInfos").addValueEventListener(chatInfoListener)

        if (this::chatInfosList.isInitialized) {
            getChatHeaders(userId)
        }
    }

    private fun getChatHeaders(userId: String) {
        for(i in 1..(chatInfosList.size)) {
            getChatHeader(chatId=i.toString(), userId)
        }
    }

    fun getChatHeader(chatId: String, userId: String){
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatHeaders[chatId] = snapshot.getValue(ChatHeader::class.java)
                findChatId(userId)
                Firebase.database.reference
                    .child("chatInfos")
                    .child(chatId)
                    .removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error ar getChatHeader: ${error.message}")
                Firebase.database.reference
                    .child("chatInfos")
                    .child(chatId)
                    .removeEventListener(this)
            }
        }
        Firebase.database.reference
            .child("chatInfos").child(chatId).addValueEventListener(listener)
    }

    private fun findChatId(userId: String) {
        var myId = FirebaseAuth.getInstance().currentUser?.uid
        for (chatId in chatHeaders.keys){
            var curChatHeader = chatHeaders[chatId]
            if (curChatHeader?.firstUser == userId && curChatHeader.secondUser == myId){
                view?.onChatIdLoaded(chatId, userHeaders[userId])
            }
            if (curChatHeader?.secondUser == userId && curChatHeader.firstUser == myId){
                view?.onChatIdLoaded(chatId, userHeaders[userId])
            }
        }
    }

    override fun loadUserHeaders() {
        getUsers()
    }

    fun onUserHeadersLoaded(){
        for (key in userHeaders.keys){
            userHeaderList.add(userHeaders[key])
        }
        userHeaderList.reverse()
        view?.onUserHeadersLoaded(userHeaderList)
    }

    fun getUsers(){
        if (!this::userListener.isInitialized){
            userListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    users = snapshot.value as HashMap<String, Any>
                    getUserHeaders()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", "error ar getUsers: ${error.message}")
                }

            }
            usersReference.addValueEventListener(userListener)
        }
        if (this::users.isInitialized) {
            getUserHeaders()
        }
    }

    fun getUserHeaders(){
        for (key in users.keys) {
            getUserHeader(key)
        }
    }

    fun getUserHeader(userId: String){
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userHeaders[userId] = snapshot.getValue(UserHeader::class.java)
                    onUserHeaderLoaded()
                    usersReference
                        .child(userId)
                        .removeEventListener(this)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", "error ar getUserHeader: ${error.message}")
                    usersReference
                        .child(userId)
                        .removeEventListener(this)
                }
            }
            usersReference.child(userId).addValueEventListener(listener)
    }

    fun onUserHeaderLoaded() {
        var loaded = numLoaded.incrementAndGet()
        if (loaded == users.size) {
            onUserHeadersLoaded()
        }
    }

    fun search(query: String){
        if (query.isEmpty()){
            view?.onUserHeadersLoaded(userHeaderList)
        }else{
            var userHeaderList = ArrayList<UserHeader?>()
            for (key in userHeaders.keys) {
                var curUserHeader = userHeaders[key]
                if (curUserHeader?.nickname?.startsWith(query)!!) {
                    userHeaderList.add(userHeaders[key])
                }
            }
            userHeaderList.reverse()
            if (userHeaderList.isEmpty()){
                Toast.makeText(view as UserSearchActivity, "No user found", Toast.LENGTH_SHORT).show()
            }
            view?.onUserHeadersLoaded(userHeaderList)
        }
    }
}