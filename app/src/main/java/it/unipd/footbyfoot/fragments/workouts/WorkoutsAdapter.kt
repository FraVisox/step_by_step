package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.fragments.Helpers
import java.io.Serializable
import java.time.LocalDate


//Adapter for a single Workout
class WorkoutsAdapter(val activity: MainActivity) : ListAdapter<Workout, WorkoutsAdapter.WorkoutViewHolder>(WORKOUT_COMPARATOR) {

    //List of points of the workouts: we keep all the points and then filter them later
    private var points : List<WorkoutTrackPoint> = listOf()

    //Update the list of points, used by the WorkoutsFragment when it observes live data
    fun updatePoints(new: List<WorkoutTrackPoint>) {
        points = new
    }

    //ListAdapters need a comparator
    companion object {
        private val WORKOUT_COMPARATOR = object : DiffUtil.ItemCallback<Workout>() {
            //Tells if two items are the same
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem.workoutId == newItem.workoutId
            }

            //Tells if two items have the same content (same name, date and time)
            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return (oldItem.name == newItem.name &&
                        oldItem.year == newItem.year &&
                        oldItem.dayOfYear == newItem.dayOfYear &&
                        oldItem.timeOfDay == newItem.timeOfDay)
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_info, parent, false)
        return WorkoutViewHolder(view)
    }


    // Displays data at a certain position
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        //Take the workout at that position
        val record = getItem(position)

        //Make a string out of meters
        val meters= activity.getString(R.string.distance_format, record.meters)

        //Calculate time and make a string out of it
        val seconds = Helpers.getSeconds(record.time)
        val minutes = Helpers.getMinutes(record.time)
        val hours = Helpers.getHours(record.time)
        val timeText = Helpers.formatDurationToString(activity, hours, minutes, seconds)

        //Take only out workout points
        val p = points.filter {
            it.workoutId == record.workoutId
        }

        //Take speed
        val sp = if (record.time != 0L) record.meters.toFloat()/record.time else 0F

        //Take a string out of date and time
        val dateTime = Helpers.formatDateTimeToString(activity, LocalDate.ofYearDay(record.year, record.dayOfYear), record.timeOfDay)

        holder.bind(dateTime, meters, timeText, activity.getString(R.string.speed_format, sp), record.name, p, record.workoutId)
    }

    //The holder of the data of a workout
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Text views to display data
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val meters = itemView.findViewById<TextView>(R.id.meters)
        private val speed = itemView.findViewById<TextView>(R.id.speed)
        private val time = itemView.findViewById<TextView>(R.id.time)
        private val name = itemView.findViewById<TextView>(R.id.activity_name)

        fun bind(dateTime: String, m: String, tim: String, v: String, nam:String, points: List<WorkoutTrackPoint>, id: Int) {
            //Set text
            date.text = dateTime
            meters.text = m
            time.text = tim
            name.text = nam
            speed.text = v

            //Set a listener on the entire view that displays the track
            itemView.setOnClickListener {
                val intent = Intent(it.context, MapsWorkoutInfoActivity::class.java)
                intent.putExtra(MapsWorkoutInfoActivity.pointsKey, points as Serializable)
                intent.putExtra(MapsWorkoutInfoActivity.timeKey, tim)
                intent.putExtra(MapsWorkoutInfoActivity.distanceKey, m)
                intent.putExtra(MapsWorkoutInfoActivity.nameKey, nam)
                intent.putExtra(MapsWorkoutInfoActivity.idKey, id)
                it.context.startActivity(intent)
            }
        }
    }

}