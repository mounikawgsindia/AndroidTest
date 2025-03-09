package com.wingspan.androidassignment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts_items")
data class AccountsPoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var AccountName: String,
    var AccountId: String,
    var alterName:String,
    )