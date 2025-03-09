package com.wingspan.androidassignment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wingspan.androidassignment.model.AccountsPoint


@Database(entities = [AccountsPoint::class], version = 1, exportSchema = false)
abstract class AccountsDataBase : RoomDatabase() {
    abstract fun accountsPoints(): AccountsDao

    companion object {
        @Volatile
        private var INSTANCE: AccountsDataBase? = null

        fun getDatabase(context: Context): AccountsDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountsDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}