package com.chari.ic.todoapp.firebase.users

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    var image: String = "",
    var mobile: Long = 0,
    val fcmToken: String = ""
): Parcelable {

}
