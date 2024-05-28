package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.workout.Workout
import it.unipd.footbyfoot.database.workout.WorkoutTrackPoint
import it.unipd.footbyfoot.MainActivity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date


// cosi è per farla come il laboratorio di room Sotto è presente una classe Adapter normale come la abbiamo vista in classe
class WorkoutsAdapter(val activity: MainActivity) : ListAdapter<Workout, WorkoutsAdapter.WorkoutViewHolder>(WORKOUT_COMPARATOR) {

    private var points : List<WorkoutTrackPoint> = listOf()

    fun updatePoints(new: List<WorkoutTrackPoint>) {
        points = new
    }

    // Devo farlo da ciò che ho capito per usare ListAdapter
    companion object {
        private val WORKOUT_COMPARATOR = object : DiffUtil.ItemCallback<Workout>() {
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {

                // Logica per determinare se due elementi rappresentano lo stesso oggetto
                return oldItem.workoutId == newItem.workoutId
            }


            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                // Confronta se il contenuto di due oggetti è lo stesso
                // Potresti confrontare ogni campo o fare un confronto basato su una versione hash
                return oldItem.date == newItem.date //TODO:
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder.create(parent)
    }


    // Displays data at a certain position
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) { //TODO: cambia tutte le stringhe
        //TODO: metti l'ora con AM, PM
        val record = getItem(position)
        val dateOfRecords= record.date
        val meters= "${record.meters}m"
        var seconds = record.time
        var minutes: Long = (seconds / 60)
        val hours: Long = (minutes/60)
        minutes %= 60
        seconds %= 60
        val timeText = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
        val name= record.name
        val p = points.filter {
            it.workoutId == record.workoutId
        }
        val sp = if (record.time != 0L) record.meters.toDouble()/record.time else 0

        holder.bind(dateOfRecords, meters, timeText, "${"%.2f".format(sp)}m/s", name, p, record.workoutId)

    }
    // Describes an item view and its place within the RecyclerView
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(parent: ViewGroup): WorkoutViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.workout_record_item, parent, false)
                return WorkoutViewHolder(view)
            }
        }

        private val date: TextView = itemView.findViewById(R.id.date)

        private val meters = itemView.findViewById<TextView>(R.id.meters)

        private val speed = itemView.findViewById<TextView>(R.id.speed)

        private val time = itemView.findViewById<TextView>(R.id.time)

        private val name = itemView.findViewById<TextView>(R.id.activity_name)

        fun bind(dat: Date, m: String, tim: String, v: String, nam:String, points: List<WorkoutTrackPoint>, id: Int) {
            date.text = SimpleDateFormat(getString(itemView.context, R.string.date_format)).format(dat)
            meters.text = m
            time.text = tim
            name.text = nam
            speed.text = v
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