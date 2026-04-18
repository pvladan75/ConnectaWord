package com.program.connectaword.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.program.connectaword.data.db.dao.RoomDao
import com.program.connectaword.data.db.entities.LocalRoomEntity

@Database(entities = [LocalRoomEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
}