package ge.finalproject.messengerapp.presenter

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ge.finalproject.messengerapp.models.User

class UserPresenter: IUserPresenter {

    override fun getUserInfo(uid: String) {
        var listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "Error at getUserInfo: ${error.message}")
            }

        }
    }

    override fun onUserLoaded(uid: String, user: User) {

    }
}