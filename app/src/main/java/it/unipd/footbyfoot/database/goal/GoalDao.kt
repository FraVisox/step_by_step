package it.unipd.footbyfoot.database.goal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    //Insert new goal and replace if it has the same date
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    //Select goals ordered
    @Query("SElECT * FROM goals_table ORDER BY year DESC, dayOfYear DESC ")
    fun getAllGoals(): Flow<List<Goal>>
}