package it.unipd.footbyfoot.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.sqlite.db.SupportSQLiteDatabase
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.goal.GoalDao

import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutDao
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import java.time.LocalDate

@Database(entities = [Goal::class, Workout::class, WorkoutTrackPoint::class], version = 1, exportSchema = false)
abstract class RecordsRoomDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun goalDao(): GoalDao

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
                    .addCallback(RecordsDatabaseCallback(scope))
                    .build()
                INSTANCE = instance //Save the instance
                instance //Return the instance
            }
        }

        //Callback called when the database is created, to populate it if needed
        private class RecordsDatabaseCallback(private val scope: CoroutineScope
        ) : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.goalDao(), database.workoutDao())
                    }
                }
            }
        }

        // TODO: rimuovi
        suspend fun populateDatabase(goalDao: GoalDao, workoutDao: WorkoutDao) {
            /*
            var currentDate = LocalDate.now()
            goalDao.insert(Goal(currentDate.year, currentDate.dayOfYear, 500, 2400, 3000))
            currentDate = currentDate.minusDays(1L)
            goalDao.insert(Goal(currentDate.year, currentDate.dayOfYear, 100, 2100, 30))
            currentDate = currentDate.minusDays(9L)
            goalDao.insert(Goal(currentDate.year, currentDate.dayOfYear, 6000, 200, 3))
            currentDate = currentDate.minusDays(31L)
            goalDao.insert(Goal(currentDate.year, currentDate.dayOfYear, 400, 200, 3))

            val date = LocalDate.now()
            workoutDao.insert(Workout(1, "1", 10L, 1000, date.year, date.dayOfYear, "11"))
            workoutDao.insert(Workout(2, "2", 29L, 10, date.year, date.dayOfYear+1, "11"))
            workoutDao.insert(Workout(3, "3", 1L, 200, date.year, date.dayOfYear+2, "11"))
            workoutDao.insert(Workout(4, "4", 22L, 5000, date.year, date.dayOfYear+3, "11"))
            workoutDao.insert(Workout(5, "5", 10L, 100000, date.year, date.dayOfYear+7, "11"))
            workoutDao.insert(Workout(6, "6", 10L, 100000, date.year, date.dayOfYear+6, "11"))
            workoutDao.insert(Workout(7, "6", 10L, 100000, date.year, date.dayOfYear+4, "11"))
            workoutDao.insert(Workout(8, "6", 10L, 100000, date.year, date.dayOfYear+5, "11"))
            workoutDao.insert(Workout(9, "6", 10L, 100000, date.year, date.dayOfYear+8, "11"))
            workoutDao.insert(Workout(10, "6", 10L, 100000, date.year, date.dayOfYear-9, "11"))
*/
        }

    }
}


