package it.unipd.footbyfoot.fragments.maps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.ActivityResultListener
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.fragments.Helpers
import it.unipd.footbyfoot.PositionsHolder
import java.time.LocalDateTime


//Activity that saves the workout
class SaveWorkoutActivity: AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var workoutId = 1
    private var nameId = 1

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

    private var dist: Int? = null
    private var totTime: Long? = null
    private lateinit var dateTime: LocalDateTime
    private lateinit var name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_workout)

        firebaseAnalytics = Firebase.analytics

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
        val speed = if (totTime != 0L) dist!!.toFloat()/totTime!! else 0F
        vel.text = getString(R.string.workout_speed, getString(R.string.speed_format, speed))

        //Set listener on save
        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            //Update name and workout ID
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

            //Finish this activity, giving the result back to MainActivity
            val intent = Intent()
            intent.putExtra(ActivityResultListener.addWorkoutToDatabase, true)
            intent.putExtra(ActivityResultListener.workoutIDKey, workoutId)
            intent.putExtra(ActivityResultListener.nameKey, name.text.toString())
            intent.putExtra(ActivityResultListener.durationKey, totTime!!)
            intent.putExtra(ActivityResultListener.distKey, dist!!)
            intent.putExtra(ActivityResultListener.yearKey, dateTime.year)
            intent.putExtra(ActivityResultListener.dayOfYearKey, dateTime.dayOfYear)
            intent.putExtra(ActivityResultListener.timeKey, Helpers.formatTimeToString(this, dateTime))
            this.setResult(RESULT_OK, intent)
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

        //No need to set the result to canceled (is default)
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
        if (PositionsHolder.positions.isNotEmpty() && PositionsHolder.positions.first() != null) {
            val pointsBundle = Bundle()
            pointsBundle.putDouble(pointsLat, PositionsHolder.positions.first()!!.latitude)
            pointsBundle.putDouble(pointsLng, PositionsHolder.positions.first()!!.longitude)
            firebaseAnalytics.logEvent(RecordsApplication.firstPointOfWorkout, pointsBundle)
        }

        finish()
    }

}