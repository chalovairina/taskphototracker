package com.plcoding.androidstorage

import android.net.Uri
import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoResource

data class SharedStoragePhoto(
    val id: Long,
    override val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
) : PhotoResource
