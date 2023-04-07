package com.chalova.irina.todoapp.binding

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator

object AuthActivityBinding {

    @BindingAdapter("setVisibility")
    @JvmStatic
    fun setVisibility(
        view: View,
        isLoading: Boolean
    ) {
        when (view) {
            is WebView -> if (isLoading) view.visibility = GONE
            else view.visibility = VISIBLE
            is CircularProgressIndicator -> if (isLoading) view.visibility = VISIBLE
            else view.visibility = GONE
        }
    }

}