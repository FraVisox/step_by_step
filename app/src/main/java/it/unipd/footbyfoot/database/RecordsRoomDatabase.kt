package it.unipd.footbyfoot.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.goal.GoalDao
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.userinfo.UserInfoDao

import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutDao
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint

@Database(entities = [Workout::class, WorkoutTrackPoint::class, Goal::class, UserInfo::class], version = 1, exportSchema = false)
abstract class RecordsRoomDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun goalDao(): GoalDao
    abstract fun infoDao(): UserInfoDao

    companion object {
        //Singleton design pattern
        @Volatile
        private var INSTANCE: RecordsRoomDatabase? = null

        fun getDatabase(context: Context): RecordsRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordsRoomDatabase::class.java,
                    context.getString(R.string.database_name)
                )
                    .build()
                INSTANCE = instance //Save the instance
                instance //Return the instance
            }
        }
    }
}


