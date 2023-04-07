package com.chalova.irina.todoapp.binding

import android.net.Uri
import android.view.View
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chalova.irina.todoapp.R
import de.hdodenhof.circleimageview.CircleImageView

object UserProfileBinding {
    @BindingAdapter("setCurrentUserData")
    @JvmStatic
    fun setCurrentUserData(
        view: View,
        imageUrl: Uri?
    ) {
        imageUrl?.let {
            when(view) {
                is CircleImageView -> {
                    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                    Glide
                        .with(view.context)
                        .load(imageUrl)
                        .apply(requestOptions)
                        .error(R.drawable.ic_user_placeholder)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(view)
                }
                else -> {}
            }
        } ?: run {
            when(view) {
                is CircleImageView -> view.setImageResource(R.drawable.ic_user_placeholder)
            }
        }
    }
}