package com.anand.mvvmskeletonarchitecture.storage.database

import android.content.Context
import android.content.SharedPreferences
import android.text.SpannableStringBuilder
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anand.mvvmskeletonarchitecture.storage.BaseDatabase
import com.anand.mvvmskeletonarchitecture.storage.DateConverter
import com.anand.mvvmskeletonarchitecture.storage.ApiCache
import com.commonsware.cwac.saferoom.SafeHelperFactory
import java.util.concurrent.Executors

const val DATABASE_NAME = "appdb"

@Database(
    entities = [ApiCache::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class
)
abstract class AppDatabase : BaseDatabase() {

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        /**
         * Utility method to run blocks on a dedicated background thread, used for io/database work.
         */
        fun ioThread(f: () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

        fun getInstance(
            context: Context,
            preferences: SharedPreferences,
            key: String
        ): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, preferences, key).also { instance = it }
            }
        }

        private fun buildDatabase(
            context: Context,
            preferences: SharedPreferences,
            key: String
        ): AppDatabase {
            val factory = SafeHelperFactory.fromUser(SpannableStringBuilder(key))

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .openHelperFactory(factory)
                //.addMigrations(Migration_1_TO_2(context), Migration_2_TO_3(context))
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ioThread {
                            // Setup initial state of DB here
                        }
                    }
                })
                .build()
        }
    }
}