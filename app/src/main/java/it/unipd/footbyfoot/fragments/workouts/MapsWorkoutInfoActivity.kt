package it.unipd.footbyfoot.fragments.workouts

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import it.unipd.footbyfoot.fragments.maps.manager.MapsManager

class MapsWorkoutInfoActivity : AppCompatActivity(), OnMapReadyCallback {
    //Keys to pass data to the intent
    companion object {
        const val pointsKey = "points"
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val nameKey = "name"
        const val idKey = "id"
        const val toastShowed = "toast"
    }

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    //Points of the workout
    private lateinit var points: List<WorkoutTrackPoint>

    //Says if the toast has already been showed
    private var showedToast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_info)

        if (savedInstanceState != null) {
            showedToast = savedInstanceState.getBoolean(toastShowed)
        }

        //This is deprecated from API level 33, but our test was on API level 32
        points = intent.getSerializableExtra(pointsKey) as List<WorkoutTrackPoint>

        //Creates the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.summary_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        //Sets things passed on the intent
        val time = findViewById<TextView>(R.id.summary_time_tv)
        time.text = intent.getStringExtra(timeKey)
        val distance = findViewById<TextView>(R.id.summary_distance_tv)
        distance.text = intent.getStringExtra(distanceKey)
        val name = findViewById<EditText>(R.id.activity_name_summary)
        val currentName = intent.getStringExtra(nameKey)
        name.setText(currentName, TextView.BufferType.EDITABLE)

        val workoutId = intent.getIntExtra(idKey, RecordsViewModel.invalidWorkoutID)

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
            val bundle = Bundle() //TODO: passiamo anche la data?
            bundle.putLong(timeKey, intent.getLongExtra(timeKey, 0))
            bundle.putInt(distanceKey, intent.getIntExtra(distanceKey, 0))
            if (points.isNotEmpty()) {
                bundle.putDoubleArray(pointsKey,
                    doubleArrayOf(points.first().latitude, points.first().longitude)
                )
            }
            RecordsApplication.firebaseAnalytics.logEvent("workout_not_saved", bundle)
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
    private lateinit var map: GoogleMap

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
        //Points are ordered because of the way we select them
        if (points.isEmpty()) {
            if (!showedToast) {
                Toast.makeText(this, getString(R.string.points_not_available), Toast.LENGTH_SHORT)
                    .show()
                showedToast = true
            }
            return
        }
        var options: PolylineOptions = defaultOptions()
        for (p in points) {
            if (p.trackList >= polylines.size) {
                options = defaultOptions()
                polylines.add(p.trackList, map.addPolyline(options.add(LatLng(p.lat, p.lng))))
            } else {
                polylines[p.trackList].remove()
                polylines[p.trackList] = map.addPolyline(options.add(LatLng(p.lat, p.lng)))
            }
        }
        //Focus on starting point
        focusPosition(LatLng(points.first().lat, points.first().lng))
    }

    //Used to focus on workout position
    private fun focusPosition(pos: LatLng) {
        map.moveCamera(CameraUpdateFactory.zoomTo(MapsManager.firstZoom))
        map.moveCamera(CameraUpdateFactory.newLatLng(pos))
    }
}