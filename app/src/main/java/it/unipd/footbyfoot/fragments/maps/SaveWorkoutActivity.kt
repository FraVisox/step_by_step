package it.unipd.footbyfoot.fragments.maps

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.fragments.Helpers
import it.unipd.footbyfoot.fragments.maps.manager.PositionsHolder
import java.time.LocalDateTime


class SaveWorkoutActivity: AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var workoutId = 1
    private var nameId = 1

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    //Keys to pass to the intent or from shared preferences
    companion object {
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val currentWorkoutID = "workoutID"
        const val currentNameID = "nameID"

        //Firebase keys for parameters
        const val pointsLat = "pointsLat"
        const val pointsLng = "pointsLng"
    }

    private var points:  MutableList<LatLng?> = mutableListOf()

    private var dist: Int? = null
    private var totTime: Long? = null
    private lateinit var dateTime: LocalDateTime
    private lateinit var name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_workout)

        firebaseAnalytics = Firebase.analytics

        //The points should be in the PositionsHolder
        points.addAll(PositionsHolder.positions)
        PositionsHolder.clearPositions()

        //Current date
        dateTime = LocalDateTime.now()

        //Get workout ID and name ID
        val preferences = getSharedPreferences(RecordsApplication.sharedWorkouts, MODE_PRIVATE)
        workoutId = preferences.getInt(currentWorkoutID, 1)
        nameId = preferences.getInt(currentNameID, 1)

        //Set views
        val time = findViewById<TextView>(R.id.save_time)
        val distance = findViewById<TextView>(R.id.save_distance)
        val vel = findViewById<TextView>(R.id.save_velocity)

        //Set the name
        name = findViewById(R.id.save_name)
        name.setText(getString(R.string.workout_name_default, nameId), TextView.BufferType.EDITABLE)

        //Set time
        totTime = intent.getLongExtra(timeKey, 0)
        val seconds = Helpers.getSeconds(totTime!!)
        val minutes = Helpers.getMinutes(totTime!!)
        val hours = Helpers.getHours(totTime!!)
        time.text = getString(R.string.workout_time, Helpers.formatDurationToString(this, hours, minutes, seconds))

        //Set distance
        dist = intent.getIntExtra(distanceKey, 0)
        distance.text = getString(R.string.workout_distance, getString(R.string.distance_format, dist))

        //Set speed
        val speed = if (totTime != 0L) dist!!.toFloat()/seconds else 0F
        vel.text = getString(R.string.workout_speed, getString(R.string.speed_format, speed))

        //Set listener on save
        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            recordsViewModel.insertWorkout(
                Workout(
                    workoutId,
                    name.text.toString(),
                    totTime!!,
                    dist!!,
                    dateTime.year,
                    dateTime.dayOfYear,
                    Helpers.formatTimeToString(this, dateTime)
                ),
                points
            )
            if (name.text.toString()
                    .contentEquals(getString(R.string.workout_name_default, nameId))
            ) {
                nameId++
            }
            workoutId++

            //Register the workout creation
            val counter = preferences.getInt(RecordsApplication.saveKey, 0) + 1
            val editor = preferences.edit()
            editor.putInt(RecordsApplication.saveKey, counter)
            editor.apply()

            val bundle = Bundle()
            bundle.putInt(RecordsApplication.saveKey, counter)
            bundle.putInt(
                RecordsApplication.addKey,
                preferences.getInt(RecordsApplication.addKey, 0)
            )
            firebaseAnalytics.logEvent(RecordsApplication.savedWorkout, bundle)

            endActivity()
        }

        //Listener on back
        val back = findViewById<ImageButton>(R.id.back_button_saveWorkout)
        back.setOnClickListener {
            backPressed()
        }

        //Listener on back of phone
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        })
    }

    fun backPressed() {
        //Workouts not saved
        val bundle = Bundle()
        bundle.putLong(timeKey, intent.getLongExtra(timeKey, 0))
        bundle.putInt(distanceKey, intent.getIntExtra(distanceKey, 0))
        firebaseAnalytics.logEvent(RecordsApplication.notSavedWorkout, bundle)

        endActivity()
    }

    private fun endActivity() {

        //Store the workout and name ID
        val preferences = getSharedPreferences(RecordsApplication.sharedWorkouts, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(currentWorkoutID, workoutId)
        editor.putInt(currentNameID, nameId)
        editor.apply()

        //First point of the workout
        if (points.isNotEmpty() && points.first() != null) {
            val pointsBundle = Bundle()
            pointsBundle.putDouble(pointsLat, points.first()!!.latitude)
            pointsBundle.putDouble(pointsLng, points.first()!!.longitude)
            firebaseAnalytics.logEvent(RecordsApplication.firstPointOfWorkout, pointsBundle)
        }

        finish()
    }

}