package com.example.roomdemo

import android.content.Context
import android.os.Build.VERSION
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Word::class),version = 1,exportSchema = false)
public  abstract class WordRoomDatabase: RoomDatabase() {
    abstract fun wordDao(): WordDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var wordDao = database.wordDao()

                    // Delete all content here.
                    wordDao.deleteAll()


                }
            }
        }
    }
    companion object{
        @Volatile
        private var INSTANCE : WordRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope
        ):WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,WordRoomDatabase::class.java,"word_database").build()
                INSTANCE = instance
                instance
            }
        }
    }


}