package com.chari.ic.todoapp.firebase

import com.chari.ic.todoapp.firebase.users.User
import com.chari.ic.todoapp.utils.Constants.USERS
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyFireStore @Inject constructor()
{
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(userInfo: User): Task<Void> {
        return fireStore.collection(USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
    }

    fun loginUser(): Task<DocumentSnapshot> {
        return fireStore.collection(USERS)
            .document(getCurrentUserId())
            .get()
    }

    internal fun getCurrentUserId(): String {
        var currentUserId = ""
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        }

        return currentUserId
    }
}