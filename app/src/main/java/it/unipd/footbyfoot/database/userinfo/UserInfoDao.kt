package it.unipd.footbyfoot.database.userinfo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: UserInfo)

    @Query("SElECT * FROM user_info_table ORDER BY year DESC, dayOfYear DESC ")
    fun getAllInfo(): Flow<List<UserInfo>>
}