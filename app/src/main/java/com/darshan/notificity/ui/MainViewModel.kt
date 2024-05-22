package com.darshan.notificity.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import com.darshan.notificity.data.model.AppInfo
import com.darshan.notificity.data.model.NotificationEntity
import com.darshan.notificity.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val application: Context, notificationRepository: NotificationRepository) :
    ViewModel() {

    private val packageManager = application.packageManager

    val notificationsFlow: Flow<List<NotificationEntity>> = notificationRepository.getAllNotificationsFlow()

    val appInfoFromFlow: Flow<List<AppInfo>> =
        notificationsFlow.map { notifications ->
            notifications
                .groupBy { it.packageName }
                .map { entry ->
                    AppInfo(
                        appName = loadAppNameFromPackageName(packageManager, entry.key),
                        icon = loadIconFromPackageName(packageManager, entry.key),
                        notificationCount = entry.value.size,
                        packageName = entry.key)
                }
        }

    val notificationsGroupedByAppFlow: Flow<Map<String, List<NotificationEntity>>> =
        notificationsFlow.map { notificationsFLow -> notificationsFLow.groupBy { it.appName } }

    private val _isNotificationPermissionGranted = MutableStateFlow(false)
    val isNotificationPermissionGranted = _isNotificationPermissionGranted.asStateFlow()

    fun refreshNotificationPermission() {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(application)
        _isNotificationPermissionGranted.update {
            enabledListeners.contains(application.packageName)
        }
    }

    init {
        refreshNotificationPermission()
    }
}

fun loadAppNameFromPackageName(packageManager: PackageManager, packageName: String): String {
    val ai: ApplicationInfo? =
        try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: NameNotFoundException) {
            null
        }
    val applicationName =
        (if (ai != null) packageManager.getApplicationLabel(ai) else "(unknown)") as String
    return applicationName
}

fun loadIconFromPackageName(packageManager: PackageManager, packageName: String): ImageBitmap? {

    val ai: ApplicationInfo? =
        try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: NameNotFoundException) {
            null
        }

    return (ai?.loadIcon(packageManager)?.toBitmap()?.asImageBitmap())
}
