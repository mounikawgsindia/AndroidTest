package com.wingspan.androidassignment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wingspan.androidassignment.model.AccountsPoint
import com.wingspan.androidassignment.model.RoomResponseModel


@Dao
interface AccountsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountsPoint(point: List<AccountsPoint>)

    @Query("SELECT * FROM accounts_items")
    suspend fun getGeoFencingPoints(): List<AccountsPoint>

    @Query("DELETE FROM accounts_items WHERE id IN (:ids)")
    suspend fun deleteGeoFencingPoints(ids: List<Int>)

    @Query("SELECT * FROM accounts_items ORDER BY id DESC LIMIT 1")
    suspend fun getLastGeoFencingPoint(): AccountsPoint?

    @Update
    fun updatePoint(point: AccountsPoint)

    @Query("UPDATE accounts_items SET alterName = :newName WHERE id = :id")
    suspend fun updateAccountName(id: Int, newName: String):Int

    @Query("DELETE FROM accounts_items")
    suspend fun deleteAllGeoFencingPoints()

    @Query("DELETE FROM accounts_items WHERE id = :id")
    suspend fun deleteAccount(id: Int): Int

    @Query("UPDATE accounts_items SET AccountName = :accountName, alterName = :alterName, AccountId = :accountId WHERE id = :id")
        suspend fun updateAccountDetails(id: Int, accountName: String, alterName: String, accountId: String): Int

}
