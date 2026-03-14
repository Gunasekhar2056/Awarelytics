package com.awarelytics.app.di

import android.content.Context
import com.awarelytics.app.data.local.AwarelyticsDatabase
import com.awarelytics.app.data.local.DriftEventDao
import com.awarelytics.app.data.local.TelemetryDao
import com.awarelytics.app.data.remote.FirebaseAuthManager
import com.awarelytics.app.data.remote.FirestoreSyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AwarelyticsDatabase {
        return AwarelyticsDatabase.getInstance(context)
    }

    @Provides
    fun provideTelemetryDao(database: AwarelyticsDatabase): TelemetryDao {
        return database.telemetryDao()
    }

    @Provides
    fun provideDriftEventDao(database: AwarelyticsDatabase): DriftEventDao {
        return database.driftEventDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthManager(): FirebaseAuthManager {
        return FirebaseAuthManager()
    }

    @Provides
    @Singleton
    fun provideFirestoreSyncManager(): FirestoreSyncManager {
        return FirestoreSyncManager()
    }
}
