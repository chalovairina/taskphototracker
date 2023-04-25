package com.chalova.irina.todoapp.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes

fun Activity.shortToast(@StringRes stringRes: Int) {
    Toast.makeText(applicationContext, stringRes, Toast.LENGTH_SHORT).show()
}

fun Activity.longToast(@StringRes stringRes: Int) {
    Toast.makeText(applicationContext, stringRes, Toast.LENGTH_LONG).show()
}

fun Activity.shortToast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.longToast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}