package it.unipd.footbyfoot.fragments.maps.manager

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.lifecycleScope
import it.unipd.footbyfoot.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//The class that manages and updates the map
class MapsManager(val context: AppCompatActivity) : OnMapReadyCallback, PositionLocationObserver {

    companion object {
        //Values of zoom
        const val firstZoom = 17F
        const val maxZoomToUpdate = 10F
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
    //Boolean to guarantee synchronized access to positions
    private var drawingTrack = false

    //Tracker that manages the binding to the service
    private val workoutTracker = WorkoutTracker(this)

    /*
     * Polylines drawn
     */
    private var currPolyline : Polyline? = null
    private var otherPolylines : MutableList<Polyline> = mutableListOf()

    //Color of the polyline
    private val trackColor : Int = Color.parseColor(getString(context, R.color.colorPrimary))

    //Returns the clean options to construct the polyline
    private fun defaultOptions(): PolylineOptions {
        return PolylineOptions().color(trackColor).geodesic(true)
    }

    //Options of current polyline (containing the points)
    private var options = defaultOptions()
    //Options of points arrived while drawing the current track
    private var justArrivedOptions = defaultOptions()

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    @Override
    override fun onMapReady(googleMap: GoogleMap) {
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
    @Override
    override fun locationUpdated(loc: Location) {
        if (drawingTrack) {
            justArrivedOptions.add(LatLng(loc.latitude, loc.longitude))
            return
        }
        if (first && mapInitialized) {
            focusPosition(loc)
            first = false
        }
        workoutTracker.updatePolyline(loc)
    }

    //Used to focus on initial position
    private fun focusPosition(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        focusPosition(pos)
    }
    private fun focusPosition(pos: LatLng) {
        if (map.cameraPosition.zoom <= maxZoomToUpdate)
            map.moveCamera(CameraUpdateFactory.zoomTo(firstZoom))
        map.animateCamera(CameraUpdateFactory.newLatLng(pos))
    }

    /*
     * Functions used to manage the workouts
     */
    //Start a new workout and set the views
    fun startWorkout(time: Chronometer, distanceView: TextView) {
        PositionTracker.addObserver(this) //Add observer, if not already in the observers
        workoutTracker.setViews(time, distanceView)
        if (PositionTracker.currentLocation == null) {
            //In this case, no workout could be initialized
            PositionTracker.startLocationTrack(context)
        } else {
            addPointToLine(PositionTracker.currentLocation!!)
        }
        workoutTracker.startWorkout()
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
    //Stop the current workout
    fun stopWorkout() {
        workoutTracker.stopWorkout()
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
    //Draw all current lines (if the service is running and the map is not updated)
    fun drawCurrentTrack(locs: List<LatLng?>) {
        if (locs.isEmpty()) {
            return
        }

        //To guarantee mutual exclusion to the options, we set drawingTrack to true
        drawingTrack = true
        justArrivedOptions = defaultOptions()
        clearLine()

        //Launch a coroutine to update the map
        context.lifecycleScope.launch(Dispatchers.IO) {
            var lastLoc: LatLng? = null
            val otherOptions: MutableList<PolylineOptions> = mutableListOf()
            locs.forEach {
                if (it == null) {
                    if (options != defaultOptions()) {
                        otherOptions.add(options)
                    }
                    options = defaultOptions()
                } else {
                    options.add(it)
                    lastLoc = it
                }
            }
            //In the UI context
            withContext(Dispatchers.Main) {
                for (o in otherOptions) {
                    otherPolylines.add(map.addPolyline(o))
                }
                //Add points arrived while drawing the track
                options.addAll(justArrivedOptions.points)
                justArrivedOptions = defaultOptions()
                currPolyline = map.addPolyline(options)
                if (first) {
                    lastLoc?.let { focusPosition(it) }
                    first = false
                }
                drawingTrack = false
            }
        }
    }
    //Deletes the lines drawn
    fun clearLine() {
        options = defaultOptions()
        currPolyline?.remove()
        currPolyline = null
        otherPolylines.forEach {
            it.remove()
        }
        otherPolylines.clear()

        //Just to be sure, remove every possible polyline: this is done as
        //if the user makes a rage tap, some lines could be drawn twice
        if (mapInitialized)
            map.clear()
    }

    /*
     * Functions used in onStop and onStart
     */
    fun stopView() {
        PositionTracker.removeObserver(this)
        clearLine()
    }
    fun takeOnWorkout() {
        workoutTracker.takeOnWorkout()
        PositionTracker.addObserver(this)
    }
}