package com.chari.ic.todoapp.utils

import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

fun matchesAndroidHome(): Matcher<View?>? {
    return CoreMatchers.allOf(
        ViewMatchers.withParent(ViewMatchers.withClassName(Matchers.`is`(Toolbar::class.java.name))),
        ViewMatchers.withClassName(
            Matchers.anyOf(
                Matchers.`is`(ImageButton::class.java.name),
                Matchers.`is`(AppCompatImageButton::class.java.name)
            )
        )
    )
}