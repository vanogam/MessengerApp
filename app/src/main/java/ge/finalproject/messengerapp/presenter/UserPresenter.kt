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
    var userHeaders = HashMap<String, UserHeader?>()
    val numLoaded = AtomicInteger(0)
    var userHeaderList = ArrayList<UserHeader?>()

//    private var lastId: String? = null
//    var list = ArrayList<UserHeader?>()


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
        }else {
            var userHeaderList = ArrayList<UserHeader?>()
            for (key in userHeaders.keys) {
                var curUserHeader = userHeaders[key]
                if (curUserHeader?.nickname?.startsWith(query)!!) {
                    userHeaderList.add(userHeaders[key])
                }
            }
            Log.d("LISTY", userHeaderList.toString())
            userHeaderList.reverse()
            if (userHeaderList.isEmpty()){
                Toast.makeText(view as UserSearchActivity, "No user found", Toast.LENGTH_SHORT).show()
            }
            view?.onUserHeadersLoaded(userHeaderList)
        }
    }
}