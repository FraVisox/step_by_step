package com.example.room.database

import androidx.annotation.WorkerThread
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
import com.google.android.gms.maps.model.LatLng

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single


class RecordsRepository(
    private val userDao: UserDao,
    private val stepsDao: StepsDao,
    private val caloriesDao: CaloriesDao,
    private val distanceDao: DistanceDao,
    private val goalDao: GoalDao,
    private val workoutDao: WorkoutDao
) {

    // tutti i records
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllActivitiesOrderedByDate()
    val allPoints: Flow<List<WorkoutTrackPoint>> = workoutDao.getAllPoints()

    val allSteps: Flow<List<Steps>> = stepsDao.getAllStepsOrderedByDate()
    val allCalories: Flow<List<Calories>> = caloriesDao.getAllCaloriesOrderedByDate()
    val allDistances: Flow<List<Distance>> = distanceDao.getAllDistancesOrderedByDate()

    // Records giornalieri
    val dailySteps: Flow<List<Steps>> = getLastXSteps(stepsDao.getAllStepsOrderedByDate(),1)
    val dailyCalories: Flow<List<Calories>> = getLastXCalories(caloriesDao.getAllCaloriesOrderedByDate(),1)
    val lastDistance: Flow<List<Distance>> = getLastXDistance(distanceDao.getAllDistancesOrderedByDate(),1)

    // Records settimanali
    val last7Steps: Flow<List<Steps>> = getLastXSteps(stepsDao.getAllStepsOrderedByDate(),7)
    val last7Calories: Flow<List<Calories>> = getLastXCalories(caloriesDao.getAllCaloriesOrderedByDate(),7)
    val last7Distances: Flow<List<Distance>> = getLastXDistance(distanceDao.getAllDistancesOrderedByDate(),7)


    // Records mensili
    val monthlySteps: Flow<List<Steps>> = getLastXSteps(stepsDao.getAllStepsOrderedByDate(),30)
    val monthlyCalories: Flow<List<Calories>> = getLastXCalories(caloriesDao.getAllCaloriesOrderedByDate(),30)
    val last30Distances: Flow<List<Distance>> = getLastXDistance(distanceDao.getAllDistancesOrderedByDate(),30)


    //goalAttuali
    val userGoals : Flow<List<Goal>> = goalDao.getAllGoals()

    private fun getLastXSteps(allSteps: Flow<List<Steps>>, x: Int): Flow<List<Steps>> {
        return allSteps.map { stepsList ->
            stepsList.takeLast(x) // se ci sono meno elementi dei richiesti ritorna tutti gli elementi
        }
    }
    private fun getLastXCalories(allCalories: Flow<List<Calories>>, x: Int): Flow<List<Calories>> {
        return allCalories.map { caloriesList ->
            caloriesList.takeLast(x)
        }
    }
    private fun getLastXDistance(allDistance: Flow<List<Distance>>, x: Int): Flow<List<Distance>> {
        return allDistance.map { distanceList ->
            distanceList.takeLast(x)
        }
    }

    fun getUserRecords(): Flow<List<UserRecords>> {
        return combine(
            last7Steps,
            last7Calories,
            last7Distances
        ) { stepsList, caloriesList, distancesList ->
            // Create a list of UserRecords.
            val userRecordsList = mutableListOf<UserRecords>()

            // Iterate over each element of the lists.
            for (i in stepsList.indices) {
                val steps = stepsList.getOrNull(i)
                val calories = caloriesList.getOrNull(i)
                val distance = distancesList.getOrNull(i)

                // Check if all records belong to the same user and have the same date.
                if (steps != null && calories != null && distance != null &&
                    steps.userId == calories.userId && steps.userId == distance.userId &&
                    steps.date == calories.date && steps.date == distance.date
                ) {
                    userRecordsList.add(UserRecords(steps.userId, steps, calories, distance))
                }
            }

            userRecordsList
        }
    }


    // Inserisci un nuovo utente
    @WorkerThread
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    // Inserisci nuovi step
    @WorkerThread
    suspend fun insertStep(step: Steps) {
        stepsDao.insert(step)
    }

    // Inserisci nuove calorie
    @WorkerThread
    suspend fun insertCalorie(calorie: Calories) {
        caloriesDao.insert(calorie)
    }

    // Inserisci una nuova distanza
    @WorkerThread
    suspend fun insertDistance(distance: Distance) {
        distanceDao.insert(distance)
    }

    // Inserisci una nuova attività
    @WorkerThread
    suspend fun insertWorkout(workout: Workout, points: MutableList<LatLng>) {
        workoutDao.insert(workout)
        points.forEach {
            workoutDao.insert(WorkoutTrackPoint(points.indexOf(it), workout.workoutId, it.latitude, it.longitude))
        }
    }

    @WorkerThread
    fun getWorkoutPoints(workout: Workout) : Flow<List<WorkoutTrackPoint>> {
        return workoutDao.getPointsOfWorkout(workout.workoutId)
    }

    @WorkerThread
    suspend fun insertGoal(goal: Goal)  {
        goalDao.insert(goal)
    }

    @WorkerThread
    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    @WorkerThread
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    // Inserisci una nuova distanza
    @WorkerThread
    suspend fun insertDayRecord(step: Steps, distance: Distance, calorie: Calories) {
        if ((step.date == distance.date && step.date == calorie.date) && (step.userId == distance.userId && step.userId == calorie.userId)) {
            stepsDao.insert(step)
            caloriesDao.insert(calorie)
            distanceDao.insert(distance)
        } else {
            throw Exception("Date and user ids must be equal")
        }
    }

    private suspend fun getLastXElements(x: Int): List<Steps> {
        return dailySteps.map { stepsList ->
            if (stepsList.size <= x) {
                stepsList
            } else {
                stepsList.subList(stepsList.size - x, stepsList.size)
            }
        }.single()
    }


}



/*
       @OptIn(ExperimentalCoroutinesApi::class)
       fun getUserTodayActivityRecords(): Flow<List<UserRecords>> {
           return userDao.getAllUsers().flatMapLatest { users ->
               combine(
                   dailySteps,
                   dailyCalories,
                   dailyDistances
               ) { steps, calories, distances ->
                   users.flatMap { user ->
                       combineUserActivity(user, steps, calories, distances)
                   }
               }
           }
       }

       // flatMapLatest è utilizzato per gestire un flusso di utenti (Flow<List<User>>) e
       // trasformarlo in un flusso di record di attività (Flow<List<UserActivityRecord>>),
       // che è combinato da più sorgenti di dati (passi mensili, calorie mensili, distanze mensili).
       // L'utilizzo di flatMapLatest assicura che se il flusso di utenti emette una nuova lista (ad esempio, un utente viene aggiunto o rimosso),
       // la combinazione corrente di passi, calorie e distanze sarà ricalcolata con la nuova lista
       // di utenti. Inoltre, evita che le trasformazioni precedenti continuino a eseguire
       // se il set di utenti è stato aggiornato, mantenendo il sistema reattivo e aggiornato
       // con l'ultimo stato disponibile.
       @OptIn(ExperimentalCoroutinesApi::class)
       fun getUserWeeklyActivityRecords(): Flow<List<com.example.room.database.UserRecords>> {
           return userDao.getAllUsers().flatMapLatest { users ->
               combine(
                   weeklySteps,
                   weeklyCalories,
                   weeklyDistances
               ) { steps, calories, distances ->
                   users.flatMap { user ->
                       combineUserActivity(user, steps, calories, distances)
                   }
               }
           }
       }

       @OptIn(ExperimentalCoroutinesApi::class)
       fun getUserMonthlyActivityRecords(): Flow<List<com.example.room.database.UserRecords>> {
           return userDao.getAllUsers().flatMapLatest { users ->
               // Ogni volta che uno dei flussi originali emette un nuovo valore, combine
               // raccoglie l'ultimo valore da ciascun flusso e li combina in un singolo output.
               // Questo ti permette di avere sempre l'ultima versione aggiornata dei dati da tutti i flussi combinati.
               combine(
                   monthlySteps,
                   monthlyCalories,
                   monthlyDistances
               ) { steps, calories, distances ->
                   users.flatMap { user ->
                       combineUserActivity(user, steps, calories, distances)
                   }
               }
           }
       }

       private fun combineUserActivity(
           user: User,
           steps: List<Steps>,
           calories: List<Calories>,
           distances: List<Distance>
       ): List<UserRecords> {
           val userActivities = mutableListOf<UserRecords>()

           // prendo le calorie e distanza basati sulla combinazione di userId e date
           val caloriesMap = calories.filter { it.userId == user.userId }.associateBy { it.date }
           val distancesMap = distances.filter { it.userId == user.userId }.associateBy { it.date }

           // Itera solo sui passi dell'utente specifico
           for (step in steps.filter { it.userId == user.userId }) {
               val calorie = caloriesMap[step.date]
               val distance = distancesMap[step.date]

               // Se esistono corrispondenze valide per la data dei passi, crea un nuovo UserActivityRecord
               if (calorie != null && distance != null) {
                   userActivities.add(UserRecords(user, step, calorie, distance))
               }
           }
           return userActivities
       }


        fun getCurrentWeekSteps(): Flow<List<Steps>> {
        return flow {
            val currentDate = Date()
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // Se oggi è lunedì, considera la settimana iniziata lunedì scorso
            val daysToSubtract = if (dayOfWeek == Calendar.MONDAY) 0 else dayOfWeek - Calendar.MONDAY + if (dayOfWeek < Calendar.MONDAY) 6 else 0
            val startOfWeek = calendar.clone() as Calendar
            startOfWeek.add(Calendar.DAY_OF_YEAR, -daysToSubtract)
            val endOfWeek = calendar.clone() as Calendar
            endOfWeek.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - dayOfWeek)

            // Ottieni i passi per la settimana corrente
            val stepsList = mutableListOf<Steps>()
            dailySteps.collect { steps ->
                steps.forEach { step ->
                    if (step.date in startOfWeek.time..endOfWeek.time) {
                        stepsList.add(step)
                    }
                }
            }
            emit(stepsList)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserTodayActivityRecords(): Flow<List<UserRecords>> {
        return userDao.getAllUsers().flatMapLatest { users ->
            combine(
                dailySteps,
                dailyCalories,
                dailyDistance
            ) { steps, calories, distances ->
                users.flatMap { user ->
                    combineUserActivity(user, steps, calories, distances)
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserWeeklyActivityRecords(): Flow<List<UserRecords>> {
        return userDao.getAllUsers().flatMapLatest { users ->
            combine(
                weeklySteps,
                weeklyCalories,
                weeklyDistances
            ) { steps, calories, distances ->
                users.flatMap { user ->
                    combineUserActivity(user, steps, calories, distances)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserMonthlyActivityRecords(): Flow<List<UserRecords>> {
        return userDao.getAllUsers().flatMapLatest { users ->
            combine(
                monthlySteps,
                monthlyCalories,
                monthlyDistances
            ) { steps, calories, distances ->
                users.flatMap { user ->
                    combineUserActivity(user, steps, calories, distances)
                }
            }
        }
    }

    private fun combineUserActivity(
        user: User,
        steps: List<Steps>,
        calories: List<Calories>,
        distances: List<Distance>
    ): List<UserRecords> {
        val userActivities = mutableListOf<UserRecords>()

        val stepsMap = steps.filter { it.userId == user.userId }.groupBy { it.date }
        val caloriesMap = calories.filter { it.userId == user.userId }.groupBy { it.date }
        val distancesMap = distances.filter { it.userId == user.userId }.groupBy { it.date }

        val dates = stepsMap.keys.intersect(caloriesMap.keys).intersect(distancesMap.keys)

        for (date in dates) {
            val step = stepsMap[date]?.first()
            val calorie = caloriesMap[date]?.first()
            val distance = distancesMap[date]?.first()

            if (step != null && calorie != null && distance != null) {
                userActivities.add(UserRecords(user, step, calorie, distance))
            }
        }
        return userActivities
    }
       */