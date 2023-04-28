package com.plcoding.androidstorage

import android.graphics.Bitmap
import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoResource

data class InternalStoragePhoto(
    override val name: String,
    val bmp: Bitmap
) : PhotoResource
