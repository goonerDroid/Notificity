package com.darshan.notificity.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darshan.notificity.util.Constants
import com.darshan.notificity.data.model.NotificationDao
import com.darshan.notificity.data.model.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    // Singleton instance of the DB
    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance =
                        Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                            Constants.dbName
                        )
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }
                return instance
            }
        }

        /**
        * MIGRATION 1 -> 2
        * Change in PrimaryKey for [NotificationEntity]
         * The primary key is now composed of [NotificationEntity.id] && [NotificationEntity.packageName]
         * The [NotificationEntity.id] is now set to the notificationId which was originally sent to the NotificationManager
        * */
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `notification_new` " +
                        "(`id` INTEGER NOT NULL, " +
                        "`packageName` TEXT NOT NULL, " +
                        "`timestamp` INTEGER NOT NULL, " +
                        "`appName` TEXT NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "`content` TEXT NOT NULL, " +
                        "`imageUrl` TEXT, " +
                        "`extras` TEXT, " +
                        "PRIMARY KEY(`id`, `packageName`))")

                database.execSQL("INSERT INTO `notification_new` (id, packageName, timestamp, appName, title, content, imageUrl, extras) " +
                        "SELECT id, packageName, timestamp, appName, title, content, imageUrl, extras FROM NotificationEntity")

                database.execSQL("DROP TABLE NotificationEntity")
                database.execSQL("ALTER TABLE `notification_new` RENAME TO notification")
            }
        }
    }
}
