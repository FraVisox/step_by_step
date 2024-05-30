package it.unipd.footbyfoot.database.goal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    @Query("SElECT * FROM goals_table ORDER BY year DESC, dayOfYear DESC ")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals_table ORDER BY year DESC, dayOfYear DESC LIMIT 1")
    fun getLastGoal(): Flow<Goal>
}