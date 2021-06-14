package com.afaneca.myfin.data.db.accounts

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by me on 14/06/2021
 */
@Dao
interface UserAccountsDao {
    @Query("SELECT * FROM useraccountentity")
    fun getAll(): LiveData<List<UserAccountEntity>>

    @Query("SELECT * FROM useraccountentity WHERE account_id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<UserAccountEntity>>

    @Query(
        "SELECT * FROM useraccountentity WHERE name LIKE :name LIMIT 1"
    )
    fun findByName(name: String): UserAccountEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<UserAccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: UserAccountEntity)

    @Delete
    fun delete(user: UserAccountEntity)

    @Query("DELETE FROM useraccountentity")
    fun deleteAll()
}