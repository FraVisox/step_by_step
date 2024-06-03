package it.unipd.footbyfoot.fragments.maps.manager

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.Chronometer
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getString
import it.unipd.footbyfoot.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

//The class that manages and updates the map
class MapsManager(val context: Activity) : OnMapReadyCallback, PositionLocationObserver {

    //Color of the polyline
    private val trackColor : Int = Color.parseColor(getString(context, R.color.colorPrimary))

    companion object {
        //Values of zoom
        const val firstZoom = 17F
        const val maxZoomToUpdate = 10F
    }

    //Returns the clear options to construct the polyline
    private fun defaultOptions(): PolylineOptions {
        return PolylineOptions().color(trackColor)
    }

    //Map
    private lateinit var map: GoogleMap

    /*
     * Booleans to initialize the map and start the tracking of the position
     */
    //Boolean to check if the map has been initialized.
    private var mapInitialized = false
    //Boolean to check if has been made a request while the map was not initialized
    private var requestMade = false
    //Boolean to check if it has already been zoomed the area of the current position
    private var first = true

    //Tracker that manages the binding to the service
    private val workoutTracker = WorkoutTracker(this)

    /*
     * Polylines drawn
     */
    private var currPolyline : Polyline? = null
    private var otherPolylines : MutableList<Polyline> = mutableListOf()

    //Options of current polyline (containing the points)
    private var options = defaultOptions()

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("AAA", "map ready")
        map = googleMap
        mapInitialized = true
        PositionTracker.addObserver(this)
        if (requestMade) {
            startUpdateMap()
        }
    }

    //Start tracking of the position
    fun startUpdateMap() {
        //If the map has not been initialized, save the request
        if (!mapInitialized) {
            requestMade = true
            return
        }
        //Check the permissions and start the tracking of the position
        if(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            PositionTracker.startLocationTrack(context)
        }
    }

    //Function called by PositionTracker as this class implements PositionLocationObserver:
    //updates the polyline, if needed
    override fun locationUpdated(loc: Location) {
        if (first && mapInitialized) {
            focusPosition(loc)
            first = false
        }
        workoutTracker.updatePolyline(loc)
    }

    //Used to focus on initial position
    private fun focusPosition(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        if (map.cameraPosition.zoom <= maxZoomToUpdate)
            map.moveCamera(CameraUpdateFactory.zoomTo(firstZoom))
        map.animateCamera(CameraUpdateFactory.newLatLng(pos))
    }

    /*
     * Functions used to manage the workouts
     */
    //Start a new workout and set the views
    fun startWorkout(time: Chronometer, distanceView: TextView) {
        PositionTracker.addObserver(this)
        workoutTracker.setViews(time, distanceView)
        if (PositionTracker.currentLocation == null) {
            //In this case, no workout could be initialized
            PositionTracker.startLocationTrack(context)
        } else {
            addPointToLine(PositionTracker.currentLocation!!)
        }
        workoutTracker.startWorkout()
    }
    //End the current workout
    fun stopWorkout() {
        workoutTracker.stopWorkout()
    }
    //Pause the current workout
    fun pauseWorkout() {
        workoutTracker.pauseWorkout()
        if (currPolyline != null) {
            otherPolylines.add(currPolyline!!)
            currPolyline = null
            options = defaultOptions()
        }
    }
    //Resume the workout
    fun resumeWorkout() {
        addPointToLine(PositionTracker.currentLocation!!)
        workoutTracker.resumeWorkout()
    }

    /*
     * Functions used to draw the track of the workout
     */
    //Adds a point to the line that is drawn
    fun addPointToLine(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        currPolyline?.remove()
        options.add(pos)
        if (mapInitialized)
            currPolyline = map.addPolyline(options)
    }
    //Draw all current lines
    fun drawCurrentTrack(locs: List<LatLng?>) {
        if (locs.isEmpty()) {
            return
        }
        clearLine()
        locs.forEach {
            if (it == null) {
                if (currPolyline != null) {
                    otherPolylines.add(currPolyline!!)
                }
                currPolyline = null
                options = defaultOptions()
            } else {
                currPolyline?.remove()
                currPolyline = map.addPolyline(options.add(it))
            }
        }
    }
    //Deletes the lines drawn
    fun clearLine() {
        Log.d("AAA", "clear line $currPolyline")
        options = defaultOptions()
        currPolyline?.remove()
        otherPolylines.forEach {
            Log.d("AAA", "clear line $it")
            it.remove()
        }
        otherPolylines.clear()
    }

    fun stopView() {
        Log.d("AAA", "view stopped")
        PositionTracker.removeObserver(this)
        clearLine()
    }
}