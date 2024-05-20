package com.example.room.fragments.workouts

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.database.activities.Workout
import com.example.room.database.activities.WorkoutTrackPoint
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

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
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        Log.d("AAA", "nana")
        val record = getItem(position)
        val dateOfRecords= record.date
        val kms= record.meters.toString()
        val time= record.time.toString()
        val name= record.name
        /*val p = points.filter {
            it.workoutId == record.workoutId
        }

         */
        holder.bind(dateOfRecords.toString(), kms, time, name)

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

        private val kms = itemView.findViewById<TextView>(R.id.kms)

        private val time = itemView.findViewById<TextView>(R.id.time)

        private val name = itemView.findViewById<TextView>(R.id.activity_name)

        fun bind(dat: String, km: String, tim: String, nam:String, /*points: List<WorkoutTrackPoint> */) {
            date.text = dat
            kms.text = km
            time.text = tim
            name.text = nam
        }
    }

}