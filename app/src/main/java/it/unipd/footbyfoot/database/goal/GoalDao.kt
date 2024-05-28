package it.unipd.footbyfoot.database.goal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    @Query("SELECT steps FROM goals_table WHERE userId = :userId")
    suspend fun getStepsByUserId(userId: Int): Int?

    @Query("SELECT calories FROM goals_table WHERE userId = :userId")
    suspend fun getCaloriesByUserId(userId: Int): Int?

    @Query("SELECT distance FROM goals_table WHERE userId = :userId")
    suspend fun getDistanceByUserId(userId: Int): Double?

    @Query("SElECT * FROM goals_table")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals_table WHERE userId = :userId")
    suspend fun getGoalById(userId: Int): Goal?

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("DELETE FROM goals_table")
    suspend fun deleteAll()
}