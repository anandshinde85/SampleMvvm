package com.anand.mvvmskeletonarchitecture.networking.facts

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.anand.mvvmskeletonarchitecture.repository.CacheableResponse
import com.anand.mvvmskeletonarchitecture.repository.ONE_DAY
import com.anand.mvvmskeletonarchitecture.repository.ONE_HOUR
import com.anand.mvvmskeletonarchitecture.repository.ONE_MINUTE
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "facts_table")
@TypeConverters(RowsStateConverter::class)
@Parcelize
data class FactsResponse(
    @ColumnInfo
    val title: String,
    val rows: List<Rows>
) : Parcelable, CacheableResponse() {
    companion object {
        fun createCacheKey(baseUrl: String) = "$baseUrl$FACTS_URL"
    }

    override val shortCacheTime: Long
        get() = ONE_MINUTE

    override val longCacheTime: Long
        get() = ONE_DAY * 30

    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Parcelize
data class Rows(
    val title: String?,
    val description: String?,
    val imageHref: String?
) : Parcelable

data class FactPalette(var color: Int)