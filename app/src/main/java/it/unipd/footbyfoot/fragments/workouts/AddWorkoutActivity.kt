package it.unipd.footbyfoot.fragments.workouts

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.RecordsViewModelFactory
import it.unipd.footbyfoot.database.workout.Workout
import com.google.android.gms.maps.model.LatLng
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import it.unipd.footbyfoot.fragments.summary.Helpers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddWorkoutActivity: AppCompatActivity() {

    private var workoutId = 1
    private var nameId = 1

    lateinit var date: Button
    lateinit var timeOfDay: Button

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_workout)

        //Get workout ID
        val preferences = getPreferences(MODE_PRIVATE)
        workoutId = preferences.getInt(SaveWorkoutActivity.currentWorkoutID, 1)
        nameId = preferences.getInt(SaveWorkoutActivity.currentNameID, 1)

        val distance = findViewById<EditText>(R.id.add_distance)

        val datePicker = DatePickerFragment()
        val timePicker = TimePickerFragment()

        date = findViewById(R.id.add_date)
        date.setOnClickListener {
            datePicker.show(supportFragmentManager, "datePicker")
        }

        timeOfDay = findViewById(R.id.add_time_of_day)
        timeOfDay.setOnClickListener {
            timePicker.show(supportFragmentManager, "timePicker")
        }

        val hours = findViewById<NumberPicker>(R.id.numpicker_hours)
        hours.maxValue = 23
        val minutes = findViewById<NumberPicker>(R.id.numpicker_minutes)
        minutes.maxValue = 59
        val seconds = findViewById<NumberPicker>(R.id.numpicker_seconds)
        seconds.maxValue = 59

        //Set the name
        val name = findViewById<EditText>(R.id.add_name)
        name.setText(getString(R.string.workout_name_default, nameId), TextView.BufferType.EDITABLE)

        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            if (distance.text.isEmpty() || name.text.isEmpty()) { //TODO
                finish()
                return@setOnClickListener
            }
            recordsViewModel.insertWorkout(
                Workout(
                    workoutId,
                    name.text.toString(),
                    Helpers.getSeconds(hours.value, minutes.value, seconds.value),
                    distance.text.toString().toInt(),
                    datePicker.year,
                    datePicker.dayOfYear,
                    timePicker.hourOfDay
                ),
                mutableListOf()
            )
            if (name.text.toString().contentEquals(getString(R.string.workout_name_default, nameId))) {
                nameId++
            }
            workoutId++
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        //Store the workout ID
        val preferences = getPreferences(MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(SaveWorkoutActivity.currentWorkoutID, workoutId)
        editor.putInt(SaveWorkoutActivity.currentNameID, nameId)
        editor.apply()
    }

}