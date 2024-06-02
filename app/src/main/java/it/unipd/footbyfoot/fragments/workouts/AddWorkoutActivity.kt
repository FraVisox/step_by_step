package it.unipd.footbyfoot.fragments.workouts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import it.unipd.footbyfoot.fragments.Helpers

class AddWorkoutActivity: AppCompatActivity() {

    private var workoutId = 1
    private var nameId = 1

    lateinit var date: Button
    lateinit var timeOfDay: Button
    lateinit var time: Button

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
        val durationPicker = DurationPickerFragment()

        date = findViewById(R.id.add_date)
        date.setOnClickListener {
            datePicker.show(supportFragmentManager, "datePicker")
        }

        timeOfDay = findViewById(R.id.add_time_of_day)
        timeOfDay.setOnClickListener {
            timePicker.show(supportFragmentManager, "timePicker")
        }

        time = findViewById(R.id.add_time)
        time.setOnClickListener {
            durationPicker.show(supportFragmentManager, "durationPicker")
        }


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
                    durationPicker.duration,
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

        val back = findViewById<ImageButton>(R.id.back_button_addWorkout)
        back.setOnClickListener {
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