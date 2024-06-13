package it.unipd.footbyfoot

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout

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

        const val addWorkoutToDatabase = "add"
        const val changeWorkoutName = "changeName"
        const val deleteWorkout = "delete"
    }

    override fun onActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data ?: return
            // The intent contains certainly a workout to add
            if (intent.getBooleanExtra(addWorkoutToDatabase, false)) {
                val points = mutableListOf<LatLng?>()
                points.addAll(PositionsHolder.positions)
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
                activity.recordsViewModel.changeWorkoutName(
                    intent.getIntExtra(workoutIDKey, RecordsViewModel.invalidWorkoutID),
                    intent.getStringExtra(nameKey) ?: ""
                )
            } else if (intent.getBooleanExtra(deleteWorkout, false)) {
                activity.recordsViewModel.deleteWorkout(intent.getIntExtra(workoutIDKey, RecordsViewModel.invalidWorkoutID))
            }
        }
        PositionsHolder.clearPositions()
    }
}