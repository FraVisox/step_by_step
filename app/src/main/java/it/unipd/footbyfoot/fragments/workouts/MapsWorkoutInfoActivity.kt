package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.ActivityResultListener
import it.unipd.footbyfoot.WorkoutPointsHolder
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import it.unipd.footbyfoot.fragments.maps.manager.MapsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsWorkoutInfoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        //Keys to pass data to the intent
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val nameKey = "name"
        const val idKey = "id"
        const val distanceTextKey = "distanceText"
        const val timeTextKey = "timeText"

        //If the toast was showed (key)
        const val toastShowed = "toast"
    }

    //Points of the workout
    private var points: MutableList<WorkoutTrackPoint> = mutableListOf()

    //Says if the toast has already been showed
    private var showedToast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_info)

        //Initialize firebase
        firebaseAnalytics = Firebase.analytics

        //If the points aren't already updated, set this object as an observer
        if (!WorkoutPointsHolder.updated) {
            WorkoutPointsHolder.setObserver(this)
        } else {
            //Otherwise, update points
            points.addAll(WorkoutPointsHolder.workoutPoints)
        }

        //Check if the toast has already been shown
        if (savedInstanceState != null) {
            showedToast = savedInstanceState.getBoolean(toastShowed)
        }

        //Get id
        val workoutId = intent.getIntExtra(idKey, RecordsViewModel.invalidWorkoutID)

        //Create the map and set the callback
        val mapFragment = supportFragmentManager.findFragmentById(R.id.summary_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        //Sets things passed on the intent
        val time = findViewById<TextView>(R.id.summary_time_tv)
        time.text = intent.getStringExtra(timeTextKey)
        val distance = findViewById<TextView>(R.id.summary_distance_tv)
        distance.text = intent.getStringExtra(distanceTextKey)
        val name = findViewById<EditText>(R.id.activity_name_summary)
        val currentName = intent.getStringExtra(nameKey)
        name.setText(currentName, TextView.BufferType.EDITABLE)

        //Back button
        val back = findViewById<Button>(R.id.back_button)
        back.setOnClickListener {
            //Change the name, if needed
            if (name.text.toString() != currentName) {
                //Set result
                val intent = Intent()
                intent.putExtra(ActivityResultListener.changeWorkoutName, true)
                intent.putExtra(ActivityResultListener.workoutIDKey, workoutId)
                intent.putExtra(ActivityResultListener.nameKey, name.text.toString())
                this.setResult(RESULT_OK, intent)
            }
            finish()
        }

        //Delete workout button
        val del = findViewById<ImageButton>(R.id.delete_workout)
        del.setOnClickListener {
            //Send event to firebase
            val bundle = Bundle()
            bundle.putLong(timeKey, intent.getLongExtra(timeKey,0))
            bundle.putInt(distanceKey, intent.getIntExtra(distanceKey, 0))
            if (points.isNotEmpty()) {
                bundle.putDouble(SaveWorkoutActivity.pointsLat, points.first().lat)
                bundle.putDouble(SaveWorkoutActivity.pointsLng, points.first().lng)
            }
            firebaseAnalytics.logEvent(RecordsApplication.workoutDeleted, bundle)

            //Set result
            val intent = Intent()
            intent.putExtra(ActivityResultListener.deleteWorkout, true)
            intent.putExtra(ActivityResultListener.workoutIDKey, workoutId)
            this.setResult(RESULT_OK, intent)
            finish()
        }
    }

    //The only thing we need to save is if we have already shown the toast: the points are saved in WorkoutPointsHolder
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(toastShowed, showedToast)
    }

    //Callback if this object becomes an observer of WorkoutPointsHolder
    fun updatedPoints() {
        //Get the points of the workout
        points.addAll(WorkoutPointsHolder.workoutPoints)
        //We don't delete points from WorkoutPointsHolder as these can be useful if the user changes configuration

        //Draw lines
        drawAllLines()
    }

    /*
     * MANAGING OF THE MAP
     */

    //Map
    private var map: GoogleMap? = null

    //Default options of the polylines drawn
    private fun defaultOptions(): PolylineOptions {
        return PolylineOptions().color(Color.parseColor(
            ContextCompat.getString(
                this,
                R.color.colorPrimary
            )
        ))
    }

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        drawAllLines()
    }

    //Draw all lines
    private fun drawAllLines() {
        if (map == null) {
            return
        }
        //If there are no points, show the toast if not already shown
        if (points.isEmpty()) {
            if (!showedToast) {
                Toast.makeText(this, getString(R.string.points_not_available), Toast.LENGTH_SHORT)
                    .show()
                showedToast = true
            }
            return
        }

        //Points are ordered because of the way we select them
        //Launch a coroutine to draw the lines
        lifecycleScope.launch(Dispatchers.IO) {
            //Get options
            var options: PolylineOptions = defaultOptions()
            val listOptions: MutableList<PolylineOptions> = mutableListOf(options)
            for (p in points) {
                if (p.trackList >= listOptions.size) {
                    listOptions.add(options)
                    options = defaultOptions()
                }
                options.add(LatLng(p.lat, p.lng))
            }
            //Launch in the main thread a coroutine to draw the polylines
            withContext(Dispatchers.Main) {
                for (o in listOptions) {
                    map!!.addPolyline(o)
                }
                //Focus on starting point
                focusPosition(LatLng(points.first().lat, points.first().lng))
            }
        }
    }

    //Used to focus on workout position
    private fun focusPosition(pos: LatLng) {
        map!!.moveCamera(CameraUpdateFactory.zoomTo(MapsManager.firstZoom))
        map!!.moveCamera(CameraUpdateFactory.newLatLng(pos))
    }
}