package com.darshan.notificity.data.repository

import com.darshan.notificity.data.AppDatabase
import com.darshan.notificity.data.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val appDatabase: AppDatabase) {

    suspend fun insertNotification(notificationEntity: NotificationEntity) {
        appDatabase.notificationDao().insertNotification(notificationEntity)
    }

    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>> =
        appDatabase.notificationDao().getAllNotificationsFlow()

}
