package it.unipd.footbyfoot.database.userinfo

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user_info_table", primaryKeys = ["year","dayOfYear"])
data class UserInfo(
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "dayOfYear")
    val dayOfYear: Int,
    @ColumnInfo(name = "height")
    val height: Int,
    @ColumnInfo(name = "weight")
    val weight: Int
)