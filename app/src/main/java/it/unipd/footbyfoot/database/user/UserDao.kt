package it.unipd.footbyfoot.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow



@Dao
interface UserDao {
    // Inserisci un nuovo utente nel database
    @Insert
    suspend fun insert(user: User)

    // Recupera tutti gli utenti dal database
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<User>>

    // Trova un utente per ID
    @Query("SELECT * FROM user_table WHERE userId = :userId")
    fun getUserById(userId: Int): Flow<User>

    // Cancella tutti gli utenti dal database
    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Update
    suspend fun updateUser(user: User)
}