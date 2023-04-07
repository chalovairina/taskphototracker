package com.chalova.irina.todoapp.user_profile.ui

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule


@com.bumptech.glide.annotation.GlideModule
class GlideModule: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val memoryCacheSizeBytes = 1024 * 1024 * 3 // 3mb
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))

        val diskCacheSizeBytes = 1024 * 1024 * 3 // 3mb
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes.toLong()))

        val bitmapPoolSizeBytes = 1024 * 1024 * 3 // 3mb
        builder.setBitmapPool(LruBitmapPool(bitmapPoolSizeBytes.toLong()))
    }
}