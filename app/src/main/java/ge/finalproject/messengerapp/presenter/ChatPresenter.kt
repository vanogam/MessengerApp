package ge.finalproject.messengerapp.presenter

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.finalproject.messengerapp.models.Message
import ge.finalproject.messengerapp.view.IChatView

class ChatPresenter(val view: IChatView) : IChatPresenter {

    private val db = Firebase.database.reference
    private var currentId: String? = null
    private var currentListener: ChildEventListener? = null
    private var isLastLoaded = false
    private var isLoading = false

    override fun initListener(chatId: String) {
        if (currentId != null) {
            db.child("chats").child(currentId!!).removeEventListener(currentListener!!)
        }
        currentId = chatId
        currentListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                if (!isLastLoaded) {
                    isLastLoaded = true
                } else {
                    onMessageAdded(message!!)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val message = snapshot.getValue(Message::class.java)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        db.child("chats").child(currentId!!).limitToLast(1)
            .addChildEventListener(currentListener!!);
    }

    override fun loadChat(chatId: String, maxTime: Long, limit: Int, startIndex: Int) {
        db.child("chats").child(chatId).orderByChild("time")
            .startAt(-maxTime.toDouble())
            .limitToFirst(limit)
            .get()
            .addOnSuccessListener { result ->
                var index = 0

                result.children.forEach {
                    onMessageLoaded(startIndex + index, it.getValue(Message::class.java)!!)
                    index += 1
                }
            }
    }

    override fun sendMessage(text: String) {

        db.child("chats").child(currentId!!).push().setValue(
            Message(
                text,
                -System.currentTimeMillis(),
                FirebaseAuth.getInstance().currentUser!!.uid
            )
        )
    }

    private fun onMessageLoaded(id: Int, message: Message) {
        view.onMessageLoaded(id, message)
    }

    private fun onMessageAdded(message: Message) {
        view.onMessageAdded(message)
    }
}