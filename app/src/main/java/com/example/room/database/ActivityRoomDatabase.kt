package com.example.room.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.room.database.activities.Workout
import com.example.room.database.activities.WorkoutDao
import com.example.room.database.activities.WorkoutTrackPoint

import com.example.room.database.calories.Calories
import com.example.room.database.calories.CaloriesDao
import com.example.room.database.distance.Distance
import com.example.room.database.distance.DistanceDao
import com.example.room.database.steps.Steps
import com.example.room.database.steps.StepsDao
import com.example.room.database.user.User
import com.example.room.database.user.UserDao
import java.util.Date

@Database(entities = [User::class, Steps::class, Calories::class, Distance::class, Workout::class, WorkoutTrackPoint::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ActivityRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stepsDao(): StepsDao
    abstract fun caloriesDao(): CaloriesDao
    abstract fun distanceDao(): DistanceDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ActivityRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityRoomDatabase::class.java,
                    "app_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()           //significa che non deve salvare i dati della sessione precedente
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: ActivityRoomDatabase) {
            // Popola il database con alcuni dati di esempio TODO: rimuovi
            Log.d("AAA", "Database da popolare")
            database.userDao().insert(User(1, "John Doe", Date()))
            database.stepsDao().insert(Steps(1, 1, 1000, Date()))
            database.caloriesDao().insert(Calories(1, 1, 500, Date()))
            database.distanceDao().insert(Distance(1, 1, 3.5, Date()))
            database.workoutDao().insert(Workout(1,1,"UGO",10,10.0,Date()))
            database.workoutDao().insert(WorkoutTrackPoint(0,1,10.0,10.0))
            database.workoutDao().insert(WorkoutTrackPoint(1,1,10.1,10.1))
            database.workoutDao().insert(WorkoutTrackPoint(2,1,10.2,10.2))
            database.workoutDao().insert(WorkoutTrackPoint(3,1,10.3,10.3))
            Log.d("AAA", "Database popolato")
        }
    }
}
