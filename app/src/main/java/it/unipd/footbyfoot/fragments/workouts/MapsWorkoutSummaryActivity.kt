package it.unipd.footbyfoot.fragments.workouts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.RecordsViewModelFactory
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import it.unipd.footbyfoot.fragments.maps.manager.MapsManager

class MapsWorkoutSummaryActivity : AppCompatActivity(), OnMapReadyCallback {
    //Keys to pass data to the intent
    companion object {
        const val pointsKey = "points"
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val nameKey = "name"
        const val idKey = "id"
    }

    //TODO: merge this with the one in mainactivity

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }


    //Points of the workout
    private lateinit var points: List<WorkoutTrackPoint>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout_info)

        //This is deprecated from API level 33, but our test was on API level 32
        points = intent.getSerializableExtra(pointsKey) as List<WorkoutTrackPoint>

        //Creates the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.summary_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        //Back button
        val back = findViewById<Button>(R.id.back_button)
        back.setOnClickListener {
            finish()
        }

        //Sets things passed
        val time = findViewById<TextView>(R.id.summary_time_tv)
        time.text = intent.getStringExtra(timeKey)
        val distance = findViewById<TextView>(R.id.summary_distance_tv)
        distance.text = intent.getStringExtra(distanceKey)
        val name = findViewById<TextView>(R.id.activity_name_summary)
        name.text = intent.getStringExtra(nameKey)

        //Delete workout button
        val del = findViewById<Button>(R.id.delete_workout)
        del.setOnClickListener {
            recordsViewModel.deleteWorkout(intent.getIntExtra(idKey, RecordsViewModel.invalidWorkoutID))
            finish()
        }
    }

    //Map
    private lateinit var map: GoogleMap

    //Default options of the polylines
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
        Log.d("AAA", "map ready")
        drawAllLines()
    }

    //Draw all lines
    private fun drawAllLines() {
        //TODO: sono passate in ordine? Sicuro?
        Log.d("AAA", points.toString())
        if (points.isEmpty())
            return
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