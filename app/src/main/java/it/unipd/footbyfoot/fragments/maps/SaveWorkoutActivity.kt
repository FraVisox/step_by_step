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
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDateTime

class SaveWorkoutActivity: AppCompatActivity() {

    private var workoutId = 1
    private var nameId = 1

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

        const val filename = "Saved_workouts"
        const val workoutsFromMap = "fromMap"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_workout)

        //Current date
        val dateTime = LocalDateTime.now()

        //Get workout ID and name ID
        val preferences = getPreferences(MODE_PRIVATE)
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
            finish()

            //Register the workout creation
            val sharedPreferences= getSharedPreferences(filename, MODE_PRIVATE)
            var counter = sharedPreferences.getInt(workoutsFromMap, 0) +1
            val editor= sharedPreferences.edit()
            editor.putInt(workoutsFromMap, counter)
            editor.apply()
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
        val preferences = getPreferences(MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(currentWorkoutID, workoutId)
        editor.putInt(currentNameID, nameId)
        editor.apply()
    }

}