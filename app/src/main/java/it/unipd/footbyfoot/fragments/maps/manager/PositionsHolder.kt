package it.unipd.footbyfoot.fragments.maps.manager

import com.google.android.gms.maps.model.LatLng

//Class that simply holds positions of workouts (instead of passing them in an Intent,
//which could cause crashes)
object PositionsHolder {
    var positions: MutableList<LatLng?> = mutableListOf()

    fun clearPositions() {
        positions.clear()
    }
}