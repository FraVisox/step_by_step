package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.fragments.summary.Helpers
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

            //Tells if two items have the same content
            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                //We check the dates and the user: one user can't have done two activities simultaneously
                return oldItem.workoutId == newItem.workoutId //TODO: cambia?
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_record_item, parent, false)
        return WorkoutViewHolder(view)
    }


    // Displays data at a certain position
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        //Take the workout at that position
        val record = getItem(position)

        //Make a string out of time, meters, speed, and take the points
        val meters= activity.getString(R.string.distance_format, record.meters)

        var seconds = record.time
        var minutes: Long = (seconds / 60)
        val hours: Long = (minutes/60)
        minutes %= 60
        seconds %= 60
        //TODO: long da problemi?
        val timeText = activity.getString(R.string.time_format, hours, minutes, seconds)

        val p = points.filter {
            it.workoutId == record.workoutId
        }

        val sp = if (record.time != 0L) record.meters.toFloat()/record.time else 0F



        holder.bind(LocalDate.ofYearDay(record.year, record.dayOfYear), meters, timeText, activity.getString(R.string.speed_format, sp), record.name, p, record.workoutId)
    }

    //The holder of the data of a workout
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Text views to display data
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val meters = itemView.findViewById<TextView>(R.id.meters)
        private val speed = itemView.findViewById<TextView>(R.id.speed)
        private val time = itemView.findViewById<TextView>(R.id.time)
        private val name = itemView.findViewById<TextView>(R.id.activity_name)

        fun bind(dat: LocalDate, m: String, tim: String, v: String, nam:String, points: List<WorkoutTrackPoint>, id: Int) {
            //Set text
            date.text = Helpers.formatDateToString(dat)
            meters.text = m
            time.text = tim
            name.text = nam
            speed.text = v

            //Set a listener on the entire view that displays the track
            itemView.setOnClickListener {
                val intent = Intent(it.context, MapsWorkoutSummaryActivity::class.java)
                intent.putExtra(MapsWorkoutSummaryActivity.pointsKey, points as Serializable)
                intent.putExtra(MapsWorkoutSummaryActivity.timeKey, tim)
                intent.putExtra(MapsWorkoutSummaryActivity.distanceKey, m)
                intent.putExtra(MapsWorkoutSummaryActivity.nameKey, nam)
                intent.putExtra(MapsWorkoutSummaryActivity.idKey, id)
                it.context.startActivity(intent)
            }
        }
    }

}