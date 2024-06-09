package it.unipd.footbyfoot.fragments.workouts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.fragments.Helpers
import it.unipd.footbyfoot.fragments.maps.SaveWorkoutActivity
import it.unipd.footbyfoot.fragments.maps.TrackWorkoutService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class AddWorkoutActivity: AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        const val durationKey = "duration"
        const val timeOfDayHOURKey = "timeOfDayHOUR"
        const val timeOfDayMINUTEKey = "timeOfDayMINUTE"
        const val dateYearKey = "dateYear"
        const val dateDayKey = "dateDay"
        const val savedDuration = "dur"
        const val savedTime = "tim"
        const val savedDate = "dat"

        //Firebase const for event
        const val daysFromWorkoutKey = "days_from_workout"
        const val numberWorkoutsKey = "number_workouts_added"
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

        firebaseAnalytics = Firebase.analytics

        //Get workout ID and name ID
        val preferences = getSharedPreferences(RecordsApplication.sharedWorkouts, MODE_PRIVATE)
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
            //Restore date
            if (savedInstanceState.getBoolean(savedDate)) {
                datePicker.year = savedInstanceState.getInt(dateYearKey)
                datePicker.dayOfYear = savedInstanceState.getInt(dateDayKey)
                date.text = Helpers.formatDateToString(
                    this,
                    LocalDate.ofYearDay(datePicker.year!!, datePicker.dayOfYear!!)
                )
            }
            //Restore time
            if (savedInstanceState.getBoolean(savedTime)) {
                timePicker.hour = savedInstanceState.getInt(timeOfDayHOURKey)
                timePicker.minute = savedInstanceState.getInt(timeOfDayMINUTEKey)
                timePicker.hourOfDay =
                    Helpers.formatTimeToString(this, timePicker.hour!!, timePicker.minute!!)
                timeOfDay.text = timePicker.hourOfDay
            }
            //Restore duration
            if (savedInstanceState.getBoolean(savedDuration)) {
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
            val now = LocalDateTime.now()
            if (datePicker.year == now.year && datePicker.dayOfYear == now.dayOfYear && (timePicker.hour!! > now.hour || (timePicker.hour == now.hour && timePicker.minute!! > now.minute))) {
                Toast.makeText(this, getString(R.string.impossible_date), Toast.LENGTH_SHORT).show()
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

            //Register the workout creation
            val counter = preferences.getInt(RecordsApplication.addKey, 0)+1
            val editor= preferences.edit()
            editor.putInt(RecordsApplication.addKey, counter)
            editor.apply()

            //Get the days from the workout
            val daysFromWorkout = ChronoUnit.DAYS.between(LocalDate.ofYearDay(datePicker.year!!, datePicker.dayOfYear!!), LocalDate.now())

            //Create a bundle and log the event
            val bundle = Bundle()
            bundle.putLong(daysFromWorkoutKey, daysFromWorkout)
            bundle.putInt(RecordsApplication.addKey, counter)
            bundle.putInt(RecordsApplication.saveKey, preferences.getInt(RecordsApplication.saveKey, 0))
            firebaseAnalytics.logEvent(RecordsApplication.addedWorkout, bundle)

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
        val preferences = getSharedPreferences(RecordsApplication.sharedWorkouts, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(SaveWorkoutActivity.currentWorkoutID, workoutId)
        editor.putInt(SaveWorkoutActivity.currentNameID, nameId)
        editor.apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Save duration, if set
        if (durationPicker.duration != DurationPickerFragment.defaultDuration) {
            outState.putLong(durationKey, durationPicker.duration)
            outState.putBoolean(savedDuration, true)
        } else {
            outState.putBoolean(savedDuration, false)
        }
        //Save date, if set
        if (datePicker.year != null && datePicker.dayOfYear != null) {
            outState.putInt(dateYearKey, datePicker.year!!)
            outState.putInt(dateDayKey, datePicker.dayOfYear!!)
            outState.putBoolean(savedDate, true)
        } else {
            outState.putBoolean(savedDate, false)
        }
        //Save time, if set
        if (timePicker.hour != null && timePicker.minute != null) {
            outState.putInt(timeOfDayHOURKey, timePicker.hour!!)
            outState.putInt(timeOfDayMINUTEKey, timePicker.minute!!)
            outState.putBoolean(savedTime, true)
        } else {
            outState.putBoolean(savedTime, false)
        }
    }

}