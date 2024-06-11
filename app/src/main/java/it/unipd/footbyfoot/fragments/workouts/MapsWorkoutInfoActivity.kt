package it.unipd.footbyfoot.fragments.workouts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import it.unipd.footbyfoot.fragments.maps.manager.MapsManager
import kotlinx.coroutines.launch

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

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    //Points of the workout
    private var points: List<WorkoutTrackPoint>? = null

    //Says if the toast has already been showed
    private var showedToast = false
    private var first = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_info)

        firebaseAnalytics = Firebase.analytics

        if (savedInstanceState != null) {
            showedToast = savedInstanceState.getBoolean(toastShowed)
        }

        val workoutId = intent.getIntExtra(idKey, RecordsViewModel.invalidWorkoutID)

        recordsViewModel.getWorkoutPoints(workoutId)?.observe(this) {
            if (first) {
                points = it
                drawAllLines()
                first = false
            }
        }

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
            if (name.text.toString() != currentName) {
                recordsViewModel.changeWorkoutName(workoutId, name.text.toString())
            }
            finish()
        }

        //Delete workout button
        val del = findViewById<ImageButton>(R.id.delete_workout)
        del.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(timeKey, intent.getLongExtra(timeKey,0))
            bundle.putInt(distanceKey, intent.getIntExtra(distanceKey, 0))
            if (points?.isNotEmpty() == true) {
                bundle.putDouble(SaveWorkoutActivity.pointsLat, points!!.first().lat)
                bundle.putDouble(SaveWorkoutActivity.pointsLng, points!!.first().lng)
            }
            firebaseAnalytics.logEvent(RecordsApplication.workoutDeleted, bundle)
            recordsViewModel.deleteWorkout(workoutId)
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

    //Polylines drawn
    private var polylines : MutableList<Polyline> = mutableListOf()

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        drawAllLines()
    }

    //Draw all lines
    private fun drawAllLines() {
        if (map == null || points == null) {
            return
        }
        //Points are ordered because of the way we select them
        if (points!!.isEmpty()) {
            if (!showedToast) {
                Toast.makeText(this, getString(R.string.points_not_available), Toast.LENGTH_SHORT)
                    .show()
                showedToast = true
            }
            return
        }
        var options: PolylineOptions = defaultOptions()
        for (p in points!!) {
            if (p.trackList >= polylines.size) {
                options = defaultOptions()
                polylines.add(p.trackList, map!!.addPolyline(options.add(LatLng(p.lat, p.lng))))
            } else {
                polylines[p.trackList].remove()
                polylines[p.trackList] = map!!.addPolyline(options.add(LatLng(p.lat, p.lng)))
            }
        }
        //Focus on starting point
        focusPosition(LatLng(points!!.first().lat, points!!.first().lng))
    }

    //Used to focus on workout position
    private fun focusPosition(pos: LatLng) {
        map!!.moveCamera(CameraUpdateFactory.zoomTo(MapsManager.firstZoom))
        map!!.moveCamera(CameraUpdateFactory.newLatLng(pos))
    }
}