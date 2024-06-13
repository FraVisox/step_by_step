package it.unipd.footbyfoot

import com.google.android.gms.maps.model.LatLng

//Class that simply holds positions (LatLng) of workouts (instead of passing them in an Intent,
//which could cause crashes when using many points)
//Used in SaveWorkoutActivity
object PositionsHolder {

    //List of positions
    var positions: MutableList<LatLng?> = mutableListOf()
        private set

    //Clear positions
    fun clearPositions() {
        positions.clear()
    }
}