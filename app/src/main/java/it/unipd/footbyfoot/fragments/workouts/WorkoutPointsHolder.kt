package it.unipd.footbyfoot.fragments.workouts

import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint

//Class that simply holds positions (WorkoutTrackPoints) of workouts (instead of passing them in an Intent,
//which could cause crashes when using many points).
//Used in MapsWorkoutInfoActivity
object WorkoutPointsHolder {

    //List of points
    var workoutPoints: MutableList<WorkoutTrackPoint> = mutableListOf()
        private set

    //Observer to notify if the points are changed
    private var observer: MapsWorkoutInfoActivity? = null

    //Boolean that indicates if the points were updated recently
    var updated = false

    //Add points to the list
    fun addAllWorkoutPoints(list: List<WorkoutTrackPoint>) {
        workoutPoints.addAll(list)
        updated = true
        observer?.updatedPoints()
    }

    //Set the current observer (only one at a time)
    fun setObserver(obj: MapsWorkoutInfoActivity) {
        observer = obj
    }

    //Clear points
    fun clearWorkoutPoints() {
        updated = false
        workoutPoints.clear()
    }
}