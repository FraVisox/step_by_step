package it.unipd.footbyfoot.database.userinfo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    //Insert new info, and replace the one existing if it has the same date
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: UserInfo)

    //Select the info of the user, ordered
    @Query("SElECT * FROM user_info_table ORDER BY year DESC, dayOfYear DESC ")
    fun getAllInfo(): Flow<List<UserInfo>>
}