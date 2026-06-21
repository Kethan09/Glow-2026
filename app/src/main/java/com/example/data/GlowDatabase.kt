package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskItemEntity::class,
        WaterLogEntity::class,
        DietLogEntity::class,
        SleepLogEntity::class,
        WorkoutLogEntity::class,
        TrackerLogEntity::class,
        ChatMessageEntity::class,
        ProgressPhotoEntity::class,
        AppStateEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GlowDatabase : RoomDatabase() {
    abstract fun glowDao(): GlowDao

    companion object {
        @Volatile
        private var INSTANCE: GlowDatabase? = null

        fun getDatabase(context: Context): GlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GlowDatabase::class.java,
                    "glowup_routine_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
