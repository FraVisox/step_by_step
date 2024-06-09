package it.unipd.footbyfoot.fragments.maps

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDateTime

class SaveWorkoutActivity: AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    private var workoutId = 1
    private var nameId = 1

    private var workoutSaved = false

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    //Keys to pass to the intent or from shared preferences
    companion object {
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val positionsKey = "positions"
        const val currentWorkoutID = "workoutID"
        const val currentNameID = "nameID"

        //Firebase keys for parameters
        const val numberWorkoutsSaved = "number_workouts_saved"
        const val pointsLat = "pointsLat"
        const val pointsLng = "pointsLng"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_workout)

        firebaseAnalytics = Firebase.analytics

        //Current date
        val dateTime = LocalDateTime.now()

        //Get workout ID and name ID
        val preferences = getSharedPreferences(RecordsApplication.sharedWorkouts, MODE_PRIVATE)
        workoutId = preferences.getInt(currentWorkoutID, 1)
        nameId = preferences.getInt(currentNameID, 1)

        //Set views
        val time = findViewById<TextView>(R.id.save_time)
        val distance = findViewById<TextView>(R.id.save_distance)
        val vel = findViewById<TextView>(R.id.save_velocity)

        //Set the name
        val name = findViewById<EditText>(R.id.save_name)
        name.setText(getString(R.string.workout_name_default, nameId), TextView.BufferType.EDITABLE)

        //Set time
        val totTime: Long = intent.getLongExtra(timeKey, 0)
        val seconds = Helpers.getSeconds(totTime)
        val minutes = Helpers.getMinutes(totTime)
        val hours = Helpers.getHours(totTime)
        time.text = getString(R.string.workout_time, Helpers.formatDurationToString(this, hours, minutes, seconds))

        //Set distance
        val dist = intent.getIntExtra(distanceKey, 0)
        distance.text = getString(R.string.workout_distance, getString(R.string.distance_format, dist))

        //Set speed
        val speed = if (totTime != 0L) dist.toFloat()/seconds else 0F
        vel.text = getString(R.string.workout_speed, getString(R.string.speed_format, speed))

        //Set listener on save
        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            workoutSaved = true
            //getSerializableExtra is used as the tests were made on Android API 32
            recordsViewModel.insertWorkout(
                Workout(
                    workoutId,
                    name.text.toString(),
                    totTime,
                    dist,
                    dateTime.year,
                    dateTime.dayOfYear,
                    Helpers.formatTimeToString(this, dateTime)
                ),
                intent.getSerializableExtra(
                    positionsKey) as MutableList<LatLng?>
            )
            if (name.text.toString().contentEquals(getString(R.string.workout_name_default, nameId))) {
                nameId++
            }
            workoutId++

            //Register the workout creation
            val counter = preferences.getInt(RecordsApplication.saveKey, 0)+1
            val editor= preferences.edit()
            editor.putInt(RecordsApplication.saveKey, counter)
            editor.apply()

            val bundle = Bundle()
            bundle.putInt(numberWorkoutsSaved, counter)
            firebaseAnalytics.logEvent(RecordsApplication.savedWorkout, bundle)

            finish()
        }

        //Listener on back
        val back = findViewById<ImageButton>(R.id.back_button_saveWorkout)
        back.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        //Store the workout and name ID
        val preferences = getSharedPreferences(RecordsApplication.sharedWorkouts, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(currentWorkoutID, workoutId)
        editor.putInt(currentNameID, nameId)
        editor.apply()

        //First point of the workout
        val points = intent.getSerializableExtra(positionsKey) as MutableList<LatLng?>
        if (points.isNotEmpty() && points.first() != null) {
            val pointsBundle = Bundle()
            pointsBundle.putDouble(pointsLat, points.first()!!.latitude)
            pointsBundle.putDouble(pointsLng, points.first()!!.longitude)
            firebaseAnalytics.logEvent(RecordsApplication.firstPointOfWorkout, pointsBundle)
        }


        //Workouts not saved
        if (!workoutSaved) {
            val bundle = Bundle()
            bundle.putLong(timeKey, intent.getLongExtra(timeKey, 0))
            bundle.putInt(distanceKey, intent.getIntExtra(distanceKey, 0))
            firebaseAnalytics.logEvent(RecordsApplication.notSavedWorkout, bundle)
        }
    }

}