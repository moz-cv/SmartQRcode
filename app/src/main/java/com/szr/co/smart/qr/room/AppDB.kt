package com.szr.co.smart.qr.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.szr.co.smart.qr.SmartApp
import com.szr.co.smart.qr.room.dao.QRDataDao
import com.szr.co.smart.qr.room.model.QRDataModel


@Database(
    entities = [QRDataModel::class], version = 1, exportSchema = false
)
abstract class AppDB : RoomDatabase() {
    companion object {
        val db by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                SmartApp.instance.applicationContext, AppDB::class.java, "smart_qr"
            ).build()
        }
    }

    abstract fun qrDataDao(): QRDataDao
}