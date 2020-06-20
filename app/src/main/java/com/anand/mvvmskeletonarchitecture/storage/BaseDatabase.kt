package com.anand.mvvmskeletonarchitecture.storage

import androidx.room.RoomDatabase

abstract class BaseDatabase : RoomDatabase() {
    abstract fun apiCacheDao(): ApiCacheDao
}