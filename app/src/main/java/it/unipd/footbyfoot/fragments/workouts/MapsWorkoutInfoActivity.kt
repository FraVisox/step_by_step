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
import it.unipd.footbyfoot.PositionsHolder
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

        //If the toast was showed
        const val toastShowed = "toast"
    }

    //Points of the workout
    private var points: MutableList<WorkoutTrackPoint> = mutableListOf()

    //Says if the toast has already been showed
    private var showedToast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_info)

        firebaseAnalytics = Firebase.analytics

        if (!PositionsHolder.updated) {
            PositionsHolder.setObserver(this)
        } else {
            updatedPoints()
        }

        //Check if the toast has already been shown
        if (savedInstanceState != null) {
            showedToast = savedInstanceState.getBoolean(toastShowed)
        }

        //Get id
        val workoutId = intent.getIntExtra(idKey, RecordsViewModel.invalidWorkoutID)

        //Creates the map
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
            val intent = Intent()
            intent.putExtra(ActivityResultListener.deleteWorkout, true)
            intent.putExtra(ActivityResultListener.workoutIDKey, workoutId)
            this.setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(toastShowed, showedToast)
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

    fun updatedPoints() {
        //Get the points of the workout: when they are available, we will draw the lines
        points.addAll(PositionsHolder.workoutPoints)
        drawAllLines()
    }

    //Draw all lines
    private fun drawAllLines() {
        if (map == null) {
            return
        }
        //Points are ordered because of the way we select them
        if (points.isEmpty()) {
            if (!showedToast) {
                Toast.makeText(this, getString(R.string.points_not_available), Toast.LENGTH_SHORT)
                    .show()
                showedToast = true
            }
            return
        }
        //Launch a coroutine
        lifecycleScope.launch(Dispatchers.IO) {
            var options: PolylineOptions = defaultOptions()
            val listOptions: MutableList<PolylineOptions> = mutableListOf(options)
            for (p in points) {
                if (p.trackList >= listOptions.size) {
                    listOptions.add(options)
                    options = defaultOptions()
                }
                options.add(LatLng(p.lat, p.lng))
            }
            //Launch in the main thread
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