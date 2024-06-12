package it.unipd.footbyfoot.fragments.maps.manager

import android.location.Location

//Interface of observers of PositionTracker
interface PositionLocationObserver {
    fun locationUpdated(loc: Location)
}