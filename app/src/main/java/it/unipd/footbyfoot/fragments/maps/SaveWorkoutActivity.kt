package it.unipd.footbyfoot.fragments.maps

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.RecordsViewModelFactory
import it.unipd.footbyfoot.database.workout.Workout
import com.google.android.gms.maps.model.LatLng
import java.util.Date

class SaveWorkoutActivity: AppCompatActivity() {

    //TODO: merge this with the one in mainactivity

    private val recordsViewModel : RecordsViewModel by viewModels{
        RecordsViewModelFactory((application as RecordsApplication).repository)
    }

    companion object {
        const val timeKey = "time"
        const val distanceKey = "distance"
        const val positionsKey = "positions"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.save_workout)

        val thisID = (application as RecordsApplication).workoutId

        val time = findViewById<TextView>(R.id.save_time)
        val distance = findViewById<TextView>(R.id.save_distance)
        val vel = findViewById<TextView>(R.id.save_velocity)
        val name = findViewById<EditText>(R.id.save_name)
        name.setText("Activity ${thisID}", TextView.BufferType.EDITABLE)

        var totTime: Long = intent.getLongExtra(timeKey, 0)
        var seconds = totTime.toInt()
        var minutes: Int = (seconds / 60)
        val hours: Int = (minutes/60)
        minutes %= 60
        seconds %= 60
        time.text = "TIME: ${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"

        val dist = intent.getIntExtra(distanceKey, 0)
        distance.text = "DISTANCE: ${dist}m"

        var speed = if (totTime != 0L) dist/seconds else 0
        vel.text = "SPEED: ${speed}m/s"

        val button = findViewById<Button>(R.id.save_button)
        button.setOnClickListener {
            recordsViewModel.insertWorkout(Workout(thisID, 1, name.text.toString(), totTime, dist, Date()), intent.getSerializableExtra(
                positionsKey) as MutableList<LatLng?>)
            if (name.text.toString().contentEquals("Activity ${thisID}")) {
                (application as RecordsApplication).workoutId++
            }
            finish()
        }
    }

}