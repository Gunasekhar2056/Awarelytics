package com.awarelytics.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TelemetryLog::class, DriftEvent::class],
    version = 1,
    exportSchema = false
)
abstract class AwarelyticsDatabase : RoomDatabase() {

    abstract fun telemetryDao(): TelemetryDao
    abstract fun driftEventDao(): DriftEventDao

    companion object {
        @Volatile
        private var INSTANCE: AwarelyticsDatabase? = null

        fun getInstance(context: Context): AwarelyticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AwarelyticsDatabase::class.java,
                    "awarelytics_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
