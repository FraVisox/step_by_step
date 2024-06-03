package it.unipd.footbyfoot.fragments.workouts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.fragments.Helpers
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import java.time.LocalDate

class AddWorkoutActivity: AppCompatActivity() {

    companion object {
        const val durationKey = "duration"
        const val timeOfDayHOURKey = "timeOfDayHOUR"
        const val timeOfDayMINUTEKey = "timeOfDayMINUTE"
        const val dateYearKey = "dateYear"
        const val dateDayKey = "dateDay"
    }

    private var workoutId = 1
    private var nameId = 1

    lateinit var date: Button
    lateinit var timeOfDay: Button
    lateinit var time: Button

    private lateinit var datePicker: DatePickerFragment
    private lateinit var timePicker: TimePickerFragment
    private lateinit var durationPicker: DurationPickerFragment

    private val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_workout)

        //Get workout ID and name ID
        val preferences = getPreferences(MODE_PRIVATE)
        workoutId = preferences.getInt(SaveWorkoutActivity.currentWorkoutID, 1)
        nameId = preferences.getInt(SaveWorkoutActivity.currentNameID, 1)

        //Get distance edit text
        val distance = findViewById<EditText>(R.id.add_distance)

        //Fragments to display
        datePicker = DatePickerFragment()
        timePicker = TimePickerFragment()
        durationPicker = DurationPickerFragment()

        date = findViewById(R.id.add_date)
        date.setOnClickListener {
            datePicker.show(supportFragmentManager, getString(R.string.date_tag))
        }

        timeOfDay = findViewById(R.id.add_time_of_day)
        timeOfDay.setOnClickListener {
            timePicker.show(supportFragmentManager, getString(R.string.time_tag))
        }

        time = findViewById(R.id.add_time)
        time.setOnClickListener {
            durationPicker.show(supportFragmentManager, getString(R.string.duration_tag))
        }

        //Set the name
        val name = findViewById<EditText>(R.id.add_name)
        name.setText(getString(R.string.workout_name_default, nameId), TextView.BufferType.EDITABLE)

        if (savedInstanceState != null) {
            //TODO: se metto mezzanotte non funzia
            //Restore date
            if (savedInstanceState.getInt(dateYearKey) != 0 && savedInstanceState.getInt(dateDayKey) != 0) {
                datePicker.year = savedInstanceState.getInt(dateYearKey)
                datePicker.dayOfYear = savedInstanceState.getInt(dateDayKey)
                date.text = Helpers.formatDateToString(
                    this,
                    LocalDate.ofYearDay(datePicker.year!!, datePicker.dayOfYear!!)
                )
            }
            //Restore time
            if (savedInstanceState.getInt(timeOfDayHOURKey) != 0 &&
                savedInstanceState.getInt(timeOfDayMINUTEKey) != 0) {
                timePicker.hour = savedInstanceState.getInt(timeOfDayHOURKey)
                timePicker.minute = savedInstanceState.getInt(timeOfDayMINUTEKey)
                timePicker.hourOfDay =
                    Helpers.formatTimeToString(this, timePicker.hour!!, timePicker.minute!!)
                timeOfDay.text = timePicker.hourOfDay
            }
            //Restore duration
            if (savedInstanceState.getLong(durationKey) != DurationPickerFragment.defaultDuration && savedInstanceState.getLong(
                    durationKey) != 0L) {
                durationPicker.duration = savedInstanceState.getLong(durationKey)
                durationPicker.seconds = Helpers.getSeconds(durationPicker.duration)
                durationPicker.minutes = Helpers.getMinutes(durationPicker.duration)
                durationPicker.hours = Helpers.getHours(durationPicker.duration)
                time.text = Helpers.formatDurationToString(
                    this,
                    durationPicker.hours,
                    durationPicker.minutes,
                    durationPicker.seconds
                )
            }
            }

        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            if (distance.text.isEmpty() || name.text.isEmpty() || timePicker.hourOfDay == TimePickerFragment.defaultHour || durationPicker.duration == DurationPickerFragment.defaultDuration || datePicker.year == null || datePicker.dayOfYear == null) {
                Toast.makeText(this, getString(R.string.impossible_to_add_workout), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            recordsViewModel.insertWorkout(
                Workout(
                    workoutId,
                    name.text.toString(),
                    durationPicker.duration,
                    distance.text.toString().toInt(),
                    datePicker.year!!,
                    datePicker.dayOfYear!!,
                    timePicker.hourOfDay
                ),
                mutableListOf() //No points
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(durationKey, durationPicker.duration)
        if (datePicker.year != null && datePicker.dayOfYear != null) {
            outState.putInt(dateYearKey, datePicker.year!!)
            outState.putInt(dateDayKey, datePicker.dayOfYear!!)
        }
        if (timePicker.hour != null && timePicker.minute != null) {
            outState.putInt(timeOfDayHOURKey, timePicker.hour!!)
            outState.putInt(timeOfDayMINUTEKey, timePicker.minute!!)
        }
    }

}