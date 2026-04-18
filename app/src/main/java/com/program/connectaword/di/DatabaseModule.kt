package com.program.connectaword.di

import android.content.Context
import androidx.room.Room
import com.program.connectaword.data.db.AppDatabase
import com.program.connectaword.data.db.dao.RoomDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "connectaword.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRoomDao(database: AppDatabase): RoomDao {
        return database.roomDao()
    }
}