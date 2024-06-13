package it.unipd.footbyfoot

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout

//Callback for the results of the activities started. It will update the database, if needed, so that
//we only have one copy of a ViewModel
class ActivityResultListener(private val activity: MainActivity): ActivityResultCallback<ActivityResult> {

    companion object {
        //Save workout keys
        const val workoutIDKey = "workout"
        const val nameKey = "name"
        const val durationKey = "duration"
        const val distKey = "dist"
        const val yearKey = "year"
        const val dayOfYearKey = "dayOfYear"
        const val timeKey = "time"

        //Action to perform keys
        const val addWorkoutToDatabase = "add"
        const val changeWorkoutName = "changeName"
        const val deleteWorkout = "delete"
    }

    override fun onActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data ?: return
            //Check the action
            if (intent.getBooleanExtra(addWorkoutToDatabase, false)) {
                //Add the positions of PositionHolder
                val points = mutableListOf<LatLng?>()
                points.addAll(PositionsHolder.positions)
                //Insert the workout
                activity.recordsViewModel.insertWorkout(
                    Workout(
                        intent.getIntExtra(workoutIDKey, RecordsViewModel.invalidWorkoutID),
                        intent.getStringExtra(nameKey) ?: "",
                        intent.getLongExtra(durationKey, 0L),
                        intent.getIntExtra(distKey, 0),
                        intent.getIntExtra(yearKey, 0),
                        intent.getIntExtra(dayOfYearKey, 0),
                        intent.getStringExtra(timeKey) ?: ""
                    ),
                    points
                )
            } else if (intent.getBooleanExtra(changeWorkoutName, false)) {
                //Change the name
                activity.recordsViewModel.changeWorkoutName(
                    intent.getIntExtra(workoutIDKey, RecordsViewModel.invalidWorkoutID),
                    intent.getStringExtra(nameKey) ?: ""
                )
            } else if (intent.getBooleanExtra(deleteWorkout, false)) {
                //Delete workout
                activity.recordsViewModel.deleteWorkout(
                    intent.getIntExtra(workoutIDKey, RecordsViewModel.invalidWorkoutID)
                )
            }
        }
        //Anyway, clear the positions (this is needed if the user exits from SaveWorkoutActivity without saving)
        PositionsHolder.clearPositions()
    }
}