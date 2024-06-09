package it.unipd.footbyfoot.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
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

        fun getDatabase(context: Context, scope: CoroutineScope): RecordsRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordsRoomDatabase::class.java,
                    context.getString(R.string.database_name)
                )
                    //.addCallback(RecordsDatabaseCallback(scope))
                    .build()
                INSTANCE = instance //Save the instance
                instance //Return the instance
            }
        }

        /*
        //Callback called when the database is created, to populate it if needed
        private class RecordsDatabaseCallback(private val scope: CoroutineScope
        ) : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.goalDao(), database.workoutDao(), database.infoDao())
                    }
                }
            }
        }


        // TODO: rimuovi
        suspend fun populateDatabase(goalDao: GoalDao, workoutDao: WorkoutDao, infoDao: UserInfoDao) {

            val currentDate = LocalDate.now()
            //infoDao.insert(UserInfo(currentDate.year, currentDate.dayOfYear, 10, 10))
            goalDao.insert(Goal(currentDate.year, currentDate.dayOfYear-9, 4800, 350, 3000))

            val date = LocalDate.now()
            workoutDao.insert(Workout(1, "Argine Tencarola", 1800L, 1500, date.year, date.dayOfYear, "10"))
            workoutDao.insert(Workout(2, "Passeggiata", 1800L, 800, date.year, date.dayOfYear, "15"))
            workoutDao.insert(Workout(3, "Percorso le tre fonti", 1950L, 1000, date.year, date.dayOfYear-1, "18"))
            workoutDao.insert(Workout(4, "Sentiero della civetta", 2000L, 2000, date.year, date.dayOfYear-2, "18"))
            workoutDao.insert(Workout(5, "Lago di Braies", 3200L, 3300, date.year, date.dayOfYear-3, "16"))
            workoutDao.insert(Workout(6, "Monte fior, foza", 3000L, 750, date.year, date.dayOfYear-7, "11"))
            workoutDao.insert(Workout(7, "Asiago paese", 6000L, 1000, date.year, date.dayOfYear-6, "14"))
            workoutDao.insert(Workout(8, "Val Formica", 5000L, 2110, date.year, date.dayOfYear-4, "9"))
            workoutDao.insert(Workout(9, "Casa", 3200L, 5000, date.year, date.dayOfYear-5, "8"))
            workoutDao.insert(Workout(10, "Tennis Abano", 1000L, 170, date.year, date.dayOfYear-8, "16"))
            workoutDao.insert(Workout(11, "Argine Salboro", 6000L, 1600, date.year, date.dayOfYear-9, "12"))
        }
        */


    }
}


