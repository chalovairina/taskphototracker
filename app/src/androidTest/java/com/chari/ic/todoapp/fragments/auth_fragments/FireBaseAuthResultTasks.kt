package com.chari.ic.todoapp.fragments.auth_fragments

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception
import java.util.concurrent.Executor

// signIn result task, success and failure
val successTask: Task<AuthResult> = object : Task<AuthResult>() {
    override fun isComplete(): Boolean = true

    override fun isSuccessful(): Boolean = true

    override fun addOnCompleteListener(executor: Executor,
                                       onCompleteListener: OnCompleteListener<AuthResult>
    ): Task<AuthResult> {
        onCompleteListener.onComplete(this)
        return this
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun getResult(): AuthResult? {
        return null
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult? {
        return null
    }

    override fun getException(): Exception? {
        return null
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Executor,
        p1: OnSuccessListener<in AuthResult>
    ): Task<AuthResult> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Activity,
        p1: OnSuccessListener<in AuthResult>
    ): Task<AuthResult> {
        return this
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
        return failureTask
    }

    override fun addOnFailureListener(
        p0: Executor,
        p1: OnFailureListener
    ): Task<AuthResult> {
        return failureTask
    }

    override fun addOnFailureListener(
        p0: Activity,
        p1: OnFailureListener
    ): Task<AuthResult> {
        return failureTask
    }
}

val failureTask: Task<AuthResult> = object : Task<AuthResult>() {
    override fun isComplete(): Boolean = true

    override fun isSuccessful(): Boolean = false

    override fun addOnCompleteListener(executor: Executor,
                                       onCompleteListener: OnCompleteListener<AuthResult>
    ): Task<AuthResult> {
        onCompleteListener.onComplete(this)
        return this
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun getResult(): AuthResult? {
        return null
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult? {
        return null
    }

    override fun getException(): Exception? {
        return null
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Executor,
        p1: OnSuccessListener<in AuthResult>
    ): Task<AuthResult> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Activity,
        p1: OnSuccessListener<in AuthResult>
    ): Task<AuthResult> {
        return this
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
        return successTask
    }

    override fun addOnFailureListener(
        p0: Executor,
        p1: OnFailureListener
    ): Task<AuthResult> {
        return successTask
    }

    override fun addOnFailureListener(
        p0: Activity,
        p1: OnFailureListener
    ): Task<AuthResult> {
        return successTask
    }
}

// user document snapshot, success and failure
val successUserDocSnapshot: Task<DocumentSnapshot> = object: Task<DocumentSnapshot>() {
    override fun isComplete(): Boolean {
        return true
    }

    override fun isSuccessful(): Boolean {
        return true
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun getResult(): DocumentSnapshot? {
        return null
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): DocumentSnapshot? {
        return null
    }

    override fun getException(): Exception? {
        return null
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentSnapshot>): Task<DocumentSnapshot> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Executor,
        p1: OnSuccessListener<in DocumentSnapshot>
    ): Task<DocumentSnapshot> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Activity,
        p1: OnSuccessListener<in DocumentSnapshot>
    ): Task<DocumentSnapshot> {
        return this
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentSnapshot> {
        return failedUserDocSnapshot
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<DocumentSnapshot> {
        return failedUserDocSnapshot
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<DocumentSnapshot> {
        return failedUserDocSnapshot
    }
}

val failedUserDocSnapshot: Task<DocumentSnapshot> = object: Task<DocumentSnapshot>() {
    override fun isComplete(): Boolean {
        return true
    }

    override fun isSuccessful(): Boolean {
        return true
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun getResult(): DocumentSnapshot? {
        return null
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): DocumentSnapshot? {
        return null
    }

    override fun getException(): Exception? {
        return null
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentSnapshot>): Task<DocumentSnapshot> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Executor,
        p1: OnSuccessListener<in DocumentSnapshot>
    ): Task<DocumentSnapshot> {
        return this
    }

    override fun addOnSuccessListener(
        p0: Activity,
        p1: OnSuccessListener<in DocumentSnapshot>
    ): Task<DocumentSnapshot> {
        return this
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentSnapshot> {
        return successUserDocSnapshot
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<DocumentSnapshot> {
        return successUserDocSnapshot
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<DocumentSnapshot> {
        return successUserDocSnapshot
    }
}