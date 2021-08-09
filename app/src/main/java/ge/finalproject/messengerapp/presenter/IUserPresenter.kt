package ge.finalproject.messengerapp.presenter

import ge.finalproject.messengerapp.models.User

interface IUserPresenter {
    fun getUserInfo(uid: String)

    fun onUserLoaded(uid: String, user: User)
}