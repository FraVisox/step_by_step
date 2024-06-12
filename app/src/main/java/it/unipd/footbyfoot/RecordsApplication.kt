package it.unipd.footbyfoot

import android.app.Application
import it.unipd.footbyfoot.database.RecordsRepository
import it.unipd.footbyfoot.database.RecordsRoomDatabase
import it.unipd.footbyfoot.database.RecordsViewModelFactory

class RecordsApplication : Application() {

    private val database by lazy { RecordsRoomDatabase.getDatabase(this) }
    private val repository by lazy { RecordsRepository(database.goalDao(), database.workoutDao(), database.infoDao()) }

    val viewModelFactory by lazy { RecordsViewModelFactory(repository) }

    companion object {
        //Package name (required for the service)
        const val packageName = "it.unipd.footbyfoot"

        //Shared preferences of workouts added and saved
        const val sharedWorkouts = "sharedWorkouts"
        const val addKey = "fromAdd"
        const val saveKey = "fromSave"

        //Firebase events' names (the keys of the parameters are in every class that sends these events)
        const val addedWorkout = "added_workout"
        const val savedWorkout = "workout_saved"
        const val openedWithNotification = "notification_while_background"
        const val workoutDeleted = "workout_deleted"
        const val notSavedWorkout = "workout_not_saved"
        const val firstPointOfWorkout = "first_point"
        const val settingsUpdate = "settings_update"
        const val goalsUpdate = "goals_update"

        //Firebase user properties
        const val averageSpeed = "AverageSpeed"
        const val workoutCounter = "WorkoutsCounter"
        const val declaredAge = "DeclaredAge"
        const val height = "Height"
        const val weight = "Weight"
        const val caloriesGoal = "CaloriesGoal"
        const val stepsGoal = "StepsGoal"
        const val distanceGoal = "DistanceGoal"

        //Firebase traces
        const val allSummariesTrace = "all_summaries_trace"
        const val weekSummaryTrace = "week_summary_trace"
        const val todaySummaryTrace = "today_summary_trace"

        const val insertGoalTrace = "insert_goal"
        const val insertInfoTrace = "insert_info"
        const val insertWorkoutTrace = "insert_workout"
        const val changeWorkoutTrace = "change_workout"
        const val deleteWorkoutTrace = "delete_workout"
    }
}