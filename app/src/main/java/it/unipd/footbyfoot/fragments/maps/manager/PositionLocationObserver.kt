package it.unipd.footbyfoot.fragments.maps.manager

import android.location.Location

interface PositionLocationObserver {
    fun locationUpdated(loc: Location)
}