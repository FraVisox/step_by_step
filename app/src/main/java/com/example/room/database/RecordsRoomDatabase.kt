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
import com.example.room.database.goal.Goal
import com.example.room.database.goal.GoalDao

import com.example.room.database.records.calories.Calories
import com.example.room.database.records.calories.CaloriesDao
import com.example.room.database.records.distance.Distance
import com.example.room.database.records.distance.DistanceDao
import com.example.room.database.records.steps.Steps
import com.example.room.database.records.steps.StepsDao
import com.example.room.database.user.User
import com.example.room.database.user.UserDao
import com.example.room.database.workout.Workout
import com.example.room.database.workout.WorkoutDao
import com.example.room.database.workout.WorkoutTrackPoint
import java.util.Date

@Database(entities = [User::class,Steps::class, Calories::class, Distance::class, Goal::class, Workout::class, WorkoutTrackPoint::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecordsRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stepsDao(): StepsDao
    abstract fun caloriesDao(): CaloriesDao
    abstract fun distanceDao(): DistanceDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: RecordsRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RecordsRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordsRoomDatabase::class.java,
                    "records_database" // Nome del database.
                )
                    .addCallback(RecordsDatabaseCallback(scope))
                    .build()
                INSTANCE = instance // Salva l'istanza creata nella variabile INSTANCE.
                instance // Ritorna l'istanza del database.
            }
        }

        private class RecordsDatabaseCallback(private val scope: CoroutineScope
        ) : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Se desideri mantenere i dati attraverso i riavvii dell'app,
                // commenta la seguente riga.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) { // Avvia una coroutine nel Dispatcher di I/O per eseguire operazioni di database.
                        populateDatabase(database.userDao(), database.stepsDao(), database.caloriesDao(), database.distanceDao(), database.goalDao(), database.workoutDao()) // Chiama populateDatabase per inserire dati iniziali.
                    }
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao, stepsDao: StepsDao, caloriesDao: CaloriesDao, distanceDao: DistanceDao, goalDao: GoalDao, workoutDao: WorkoutDao) {

            userDao.deleteAll()
            stepsDao.deleteAll()
            caloriesDao.deleteAll()
            distanceDao.deleteAll()
            goalDao.deleteAll()
            Log.d("MainActivity00", "USto effettivamente inizializzando")

            workoutDao.insert(Workout(1,1,"UGO",10,10,Date()))
            workoutDao.insert(WorkoutTrackPoint(0,1,10.0,10.0))
            workoutDao.insert(WorkoutTrackPoint(1,1,10.1,10.1))
            workoutDao.insert(WorkoutTrackPoint(2,1,10.2,10.2))
            workoutDao.insert(WorkoutTrackPoint(3,1,10.3,10.3))

            userDao.insert(User(1, "John Doe", Date(),70,160))
            stepsDao.insert(Steps(1, 1, 1000, Date()))
            caloriesDao.insert(Calories(1, 1, 500, Date()))
            distanceDao.insert(Distance(1, 1, 3.5, Date()))
            goalDao.insert(Goal(1, 1000, 1000, 100.1))

            val users = userDao.getAllUsers()
            val steps = stepsDao.getAllStepsOrderedByDate()
            val calories = caloriesDao.getAllCaloriesOrderedByDate()
            val distances = distanceDao.getAllDistancesOrderedByDate()

            Log.d("RecordsRoomDatabase1", "Users: $users")
            Log.d("RecordsRoomDatabase2", "Steps: $steps")
            Log.d("RecordsRoomDatabase3", "Calories: $calories")
            Log.d("RecordsRoomDatabase4", "Distances: $distances")

            users.collect { user ->
                Log.d("Banana1", "user: $user")
            }
            steps.collect { step ->
                Log.d("Banana2", "Step: $step")
            }

            calories.collect { distance ->
                Log.d("Banana3", "distance: $distance")
            }

            distances.collect { calorie ->
                Log.d("Banana4", "calorie: $calorie")
            }


        }

    }
}


/*
        suspend fun populateDatabase(database: RecordsRoomDatabase) {
            .deleteAll()

            // Popola il database con alcuni dati di esempio
            userDao().insert(User(1, "John Doe", Date()))
            database.stepsDao().insert(Steps(1, 1, 1000, Date()))
            database.caloriesDao().insert(Calories(1, 1, 500, Date()))
            database.distanceDao().insert(Distance(1, 1, 3.5, Date()))

            val users = database.userDao().getAllUsers()
            val steps = database.stepsDao().getAllStepsOrderedByDate()
            val calories = database.caloriesDao().getAllCaloriesOrderedByDate()
            val distances = database.distanceDao().getAllDistancesOrderedByDate()

            Log.d("RecordsRoomDatabase1", "Users: $users")
            Log.d("RecordsRoomDatabase2", "Steps: $steps")
            Log.d("RecordsRoomDatabase3", "Calories: $calories")
            Log.d("RecordsRoomDatabase4", "Distances: $distances")
        }*/