package it.unipd.footbyfoot

import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import it.unipd.footbyfoot.fragments.workouts.MapsWorkoutInfoActivity

//Class that simply holds positions of workouts (instead of passing them in an Intent,
//which could cause crashes with many points)
object PositionsHolder {

    private var observer: MapsWorkoutInfoActivity? = null
    var positions: MutableList<LatLng?> = mutableListOf()
    var workoutPoints: MutableList<WorkoutTrackPoint> = mutableListOf()

    var updated = false

    fun addAllWorkoutPoints(list: List<WorkoutTrackPoint>) {
        workoutPoints.addAll(list)
        updated = true
        observer?.updatedPoints()
    }

    fun setObserver(obj: MapsWorkoutInfoActivity) {
        observer = obj
    }

    fun clearWorkoutPoints() {
        updated = false
        workoutPoints.clear()
    }

    fun clearPositions() {
        positions.clear()
    }
}