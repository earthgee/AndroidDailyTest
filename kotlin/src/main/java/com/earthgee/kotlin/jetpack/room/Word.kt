package com.earthgee.kotlin.jetpack.room

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 *  Created by zhaoruixuan1 on 2023/12/6
 *  CopyRight (c) haodf.com
 *  功能：Word数据库
 */
@Entity(tableName = "word_table")
data class Word(@PrimaryKey @ColumnInfo(name = "word") val word: String)

@Dao
interface WordDao {
    @Query("select * from word_table order by word ASC")
    fun getAlphabetizedWords(): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word): Long

    @Query("delete from word_table")
    suspend fun deleteAll()
}

@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)
abstract class WordRoomDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {

        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(context: Context): WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

    }
}

class WordRepository(private val wordDao: WordDao) {

    val allWords: Flow<List<Word>> = wordDao.getAlphabetizedWords()

    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }

}

object WordDataBaseUtil {

    lateinit var repository: WordRepository

    fun getWordRepository(context: Context): WordRepository {
        if(!::repository.isInitialized) {
            repository = WordRepository(WordRoomDatabase.getDatabase(context).wordDao())
        }
        return repository
    }

}