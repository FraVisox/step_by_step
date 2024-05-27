package com.example.room.fragments.workouts

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.room.R
import com.example.room.RecordsApplication
import com.example.room.database.RecordsViewModel
import com.example.room.database.RecordsViewModelFactory
import com.example.room.database.workout.WorkoutTrackPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

class MapsWorkoutSummaryActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val pointsKey = "points"
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val nameKey = "name"
        const val idKey = "id"
    }

    //TODO: merge this with the one in mainactivity

    private val recordsViewModel : RecordsViewModel by viewModels{
        RecordsViewModelFactory((application as RecordsApplication).repository)
    }

    private lateinit var points: List<WorkoutTrackPoint>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout_summary)

        //This is deprecated from API level 33, but our test was on API level 32
        points = intent.getSerializableExtra(pointsKey) as List<WorkoutTrackPoint>

        val mapFragment = supportFragmentManager.findFragmentById(R.id.summary_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        val back = findViewById<Button>(R.id.back_button)
        back.setOnClickListener {
            finish()
        }

        val time = findViewById<TextView>(R.id.summary_time_tv)
        time.text = intent.getStringExtra(timeKey)
        val distance = findViewById<TextView>(R.id.summary_distance_tv)
        distance.text = intent.getStringExtra(distanceKey)
        val name = findViewById<TextView>(R.id.activity_name_summary)
        name.text = intent.getStringExtra(nameKey)

        val del = findViewById<Button>(R.id.delete_workout)
        del.setOnClickListener {
            recordsViewModel.deleteWorkout(intent.getIntExtra(idKey, RecordsViewModel.invalidID))
            finish()
        }
    }

    //Map
    private lateinit var map: GoogleMap

    //Polylines drawn
    private var polylines : MutableList<Polyline> = mutableListOf()


    //Options of the line to draw
    private var positions: PolylineOptions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap()).endCap(RoundCap())

    //Called when the map is ready (as this class implements OnMapReadyCallback)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        drawAllLines()
    }

    //Used to focus on position initially
    private fun drawAllLines() {
        if (points.isEmpty())
            return
        var i = -1
        var first : WorkoutTrackPoint? = null
        for (p in points) {
            if (p.trackList == first?.trackList) {
                val old = polylines[i]
                polylines[i] = map.addPolyline(positions.add(LatLng(p.lat, p.lng)))
                old.remove()
                continue
            }
            first = p
            positions = PolylineOptions().color(Color.parseColor("#FF0000")).startCap(RoundCap()).endCap(RoundCap())
            polylines.add(map.addPolyline(positions.add(LatLng(p.lat, p.lng))))
            i++
        }
        focusPosition(LatLng(points[0].lat, points[0].lng))
    }

    //Used to focus on workout position
    private fun focusPosition(pos: LatLng) {
        map.moveCamera(CameraUpdateFactory.zoomTo(16F))
        map.moveCamera(CameraUpdateFactory.newLatLng(pos))
    }
}