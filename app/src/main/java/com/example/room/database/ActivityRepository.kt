package com.example.room.database

import androidx.annotation.WorkerThread
import com.example.room.database.calories.Calories
import com.example.room.database.calories.CaloriesDao
import com.example.room.database.distance.Distance
import com.example.room.database.distance.DistanceDao
import com.example.room.database.steps.Steps
import com.example.room.database.steps.StepsDao
import com.example.room.database.user.User
import com.example.room.database.user.UserDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest


class ActivityRepository(
    private val userDao: UserDao,
    private val stepsDao: StepsDao,
    private val caloriesDao: CaloriesDao,
    private val distanceDao: DistanceDao
) {

    // tutti i records
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allSteps: Flow<List<Steps>> = stepsDao.getAllStepsOrderedByDate()
    val allCalories: Flow<List<Calories>> = caloriesDao.getAllCaloriesOrderedByDate()
    val allDistances: Flow<List<Distance>> = distanceDao.getAllDistancesOrderedByDate()

    // Records giornalieri
    val dailySteps: Flow<List<Steps>> = stepsDao.getTodaySteps()
    val dailyCalories: Flow<List<Calories>> = caloriesDao.getTodayCalories()
    val dailyDistances: Flow<List<Distance>> = distanceDao.getTodayDistance()

    // Records settimanali
    val weeklySteps: Flow<List<Steps>> = stepsDao.getWeeklySteps()
    val weeklyCalories: Flow<List<Calories>> = caloriesDao.getWeeklyCalories()
    val weeklyDistances: Flow<List<Distance>> = distanceDao.getWeeklyDistances()


    // Records mensili
    val monthlySteps: Flow<List<Steps>> = stepsDao.getMonthlySteps()
    val monthlyCalories: Flow<List<Calories>> = caloriesDao.getMonthlyCalories()
    val monthlyDistances: Flow<List<Distance>> = distanceDao.getMonthlyDistances()

    val todayActivityRecords: Flow<List<UserActivityRecord>> = getUserTodayActivityRecords()
    val weeklyActivityRecords: Flow<List<UserActivityRecord>> = getUserWeeklyActivityRecords()
    val monthlyActivityRecords: Flow<List<UserActivityRecord>> = getUserMonthlyActivityRecords()


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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserTodayActivityRecords(): Flow<List<UserActivityRecord>> {
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
    fun getUserWeeklyActivityRecords(): Flow<List<UserActivityRecord>> {
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
    fun getUserMonthlyActivityRecords(): Flow<List<UserActivityRecord>> {
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
    ): List<UserActivityRecord> {
        val userActivities = mutableListOf<UserActivityRecord>()

        // Mappa per velocizzare l'accesso ai dati di calorie e distanza basati sulla combinazione di userId e date
        val caloriesMap = calories.filter { it.userId == user.userId }.associateBy { it.date }
        val distancesMap = distances.filter { it.userId == user.userId }.associateBy { it.date }

        // Itera solo sui passi dell'utente specifico
        for (step in steps.filter { it.userId == user.userId }) {
            val calorie = caloriesMap[step.date]
            val distance = distancesMap[step.date]

            // Se esistono corrispondenze valide per la data dei passi, crea un nuovo UserActivityRecord
            if (calorie != null && distance != null) {
                userActivities.add(UserActivityRecord(user, step, calorie, distance))
            }
        }
        return userActivities
    }

}
