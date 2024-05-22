package com.darshan.notificity.data.model

import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: ImageBitmap?,
    val notificationCount: Int
)
