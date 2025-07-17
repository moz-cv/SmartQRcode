package com.szr.co.smart.qr.room.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "qr_data")
data class QRDataModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "content") var content: String = "",
    @ColumnInfo(name = "type") var type: Int = -1,
    @ColumnInfo(name = "bg_id") var bgId: Int = -1,
    @ColumnInfo(name = "source") var source: Int = 0,
    @ColumnInfo(name = "add_time") var addTime: Long
): Parcelable