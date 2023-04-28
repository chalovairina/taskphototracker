package com.chalova.irina.taskphototracker.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LifecycleOwner.repeatOnState(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    this.lifecycleScope.launch {
        repeatOnLifecycle(state, block)
    }
}