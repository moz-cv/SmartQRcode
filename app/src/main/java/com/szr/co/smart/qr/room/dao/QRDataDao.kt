package com.szr.co.smart.qr.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.szr.co.smart.qr.room.model.QRDataModel

@Dao
interface QRDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: QRDataModel): Long

    @Query("SELECT * FROM qr_data WHERE source = 0 ORDER BY add_time DESC")
    fun scanQRData(): PagingSource<Int, QRDataModel>

    @Query("SELECT * FROM qr_data WHERE source = 1 ORDER BY add_time DESC")
    fun genQRData(): PagingSource<Int, QRDataModel>

    @Delete
    suspend fun deleteList(users: List<QRDataModel>): Int
}