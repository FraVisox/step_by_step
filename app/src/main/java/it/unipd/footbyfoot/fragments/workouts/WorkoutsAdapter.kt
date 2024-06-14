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
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

class WorkoutsAdapter(val activity: MainActivity) : ListAdapter<Workout, WorkoutsAdapter.WorkoutViewHolder>(WORKOUT_COMPARATOR) {

    //ListAdapters need a comparator
    companion object {
        private val WORKOUT_COMPARATOR = object : DiffUtil.ItemCallback<Workout>() {
            //Tells if two items are the same
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem.workoutId == newItem.workoutId
            }

            //Tells if two items have the same content (same name, date and time): the name is the
            //most important, as the user can change it in MapsWorkoutInfoActivity
            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return (oldItem.name == newItem.name &&
                        oldItem.year == newItem.year &&
                        oldItem.dayOfYear == newItem.dayOfYear &&
                        oldItem.timeOfDay == newItem.timeOfDay)
            }
        }
    }

    //Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_info, parent, false)
        return WorkoutViewHolder(view)
    }


    //Displays data at a certain position
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        //Take the workout at that position
        val record = getItem(position)

        //Take speed
        val sp = if (record.time != 0L) record.meters.toFloat()/record.time else 0F

        //Take a string out of date and time
        val dateTime = Helpers.formatDateTimeToString(activity, LocalDate.ofYearDay(record.year, record.dayOfYear), record.timeOfDay)

        holder.bind(dateTime, record.meters, record.time, activity.getString(R.string.speed_format, sp), record.name, record.workoutId)
    }

    //The holder of the data of a workout
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Text views to display data
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val meters = itemView.findViewById<TextView>(R.id.meters)
        private val speed = itemView.findViewById<TextView>(R.id.speed)
        private val time = itemView.findViewById<TextView>(R.id.time)
        private val name = itemView.findViewById<TextView>(R.id.activity_name)

        fun bind(dateTime: String, m: Int, tim: Long, v: String, nam:String, id: Int) {

            //Get distance text
            val metersText = itemView.context.getString(R.string.distance_format, m)

            //Get time text
            val seconds = Helpers.getSeconds(tim)
            val minutes = Helpers.getMinutes(tim)
            val hours = Helpers.getHours(tim)
            val timeText = Helpers.formatDurationToString(itemView.context, hours, minutes, seconds)

            //Set text
            date.text = dateTime
            meters.text = metersText
            time.text = timeText
            name.text = nam
            speed.text = v

            //Set a listener on the entire view that displays the track
            itemView.setOnClickListener {
                val intent = Intent(it.context, MapsWorkoutInfoActivity::class.java)
                intent.putExtra(MapsWorkoutInfoActivity.timeKey, tim)
                intent.putExtra(MapsWorkoutInfoActivity.timeTextKey, timeText)
                intent.putExtra(MapsWorkoutInfoActivity.distanceKey, m)
                intent.putExtra(MapsWorkoutInfoActivity.distanceTextKey, metersText)
                intent.putExtra(MapsWorkoutInfoActivity.nameKey, nam)
                intent.putExtra(MapsWorkoutInfoActivity.idKey, id)

                //Observe points of this workout from the database, and save them in WorkoutPointsHolder when updated for the first time
                var first = true
                //Clear the points previously contained
                WorkoutPointsHolder.clearWorkoutPoints()
                (it.context as MainActivity).recordsViewModel.getWorkoutPoints(id)?.observe(it.context as MainActivity) { list ->
                    if (first) {
                        //Update them
                        WorkoutPointsHolder.addAllWorkoutPoints(list)
                        first = false
                    }
                }

                //Start activity
                (it.context as MainActivity).startForResult.launch(intent)
            }
        }
    }

}