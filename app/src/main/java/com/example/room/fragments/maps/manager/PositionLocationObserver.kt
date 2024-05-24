package com.example.room.fragments.maps.manager

import android.location.Location

interface PositionLocationObserver {
    fun locationUpdated(loc: Location)
}