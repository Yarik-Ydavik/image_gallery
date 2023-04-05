package com.example.image_pick.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.image_pick.data.PickImage

internal class PickUriManager(private val context: Context) {

    private val photoCollection by lazy {
        if (Build.VERSION.SDK_INT > 28) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    fun getNewUri() = context.contentResolver.insert(photoCollection, setupPhotoDetails())

    // Добавление новой "PickImage" картинки

    fun getPickImage(uri: Uri?): PickImage? = uri?.let {
        PickImage(
            it,
            System.currentTimeMillis(),
            setupPhotoDetails().getAsString(MediaStore.Images.Media.DISPLAY_NAME),
            null,
            null
        )
    }

    // Добавление информации о фотографии

    private fun setupPhotoDetails() = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, getFileName())
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    private fun getFileName() = "pick-camera-${System.currentTimeMillis()}.jpg"
}