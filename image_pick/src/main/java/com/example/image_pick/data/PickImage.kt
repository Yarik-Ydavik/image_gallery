package com.example.image_pick.data

import android.net.Uri

data class PickImage (
    val uri: Uri,
    internal val dateTaken: Long?,
    val displayName: String?,
    internal val id: Long?,
    internal val folderName: String?,
)