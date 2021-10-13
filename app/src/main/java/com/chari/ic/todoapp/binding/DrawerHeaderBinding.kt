package com.chari.ic.todoapp.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.database.entities.Priority
import com.chari.ic.todoapp.firebase.users.User
import com.chari.ic.todoapp.repository.datastore.CurrentUserPreferences
import com.chari.ic.todoapp.utils.PriorityUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.Flow

object DrawerHeaderBinding {
    @BindingAdapter("setCurrentUserData")
    @JvmStatic
    fun setCurrentUserData(
        view: View,
        currentUser: CurrentUserPreferences?
    ) {
        // at first binding liveData usually produces null values
        if (currentUser == null || currentUser.userId.isEmpty()) {
            when(view) {
                is CircleImageView -> view.setImageResource(R.drawable.ic_user_placeholder)
                is TextView -> view.text = view.context.getString(R.string.my_account)
            }
            return
        }
        when(view) {
            is CircleImageView -> {
                Glide
                        .with(view.context)
                        .load(currentUser.userImageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(view)
            }
            is TextView -> view.text = currentUser.userEmail
        }
    }
}