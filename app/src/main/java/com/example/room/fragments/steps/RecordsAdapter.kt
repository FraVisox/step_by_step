package com.example.room.fragments.steps


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.database.RecordsViewModel
import com.example.room.database.UserRecords

class RecordsAdapter(private val recordsViewModel: RecordsViewModel) : ListAdapter<UserRecords, RecordsAdapter.RecordsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserRecords>() {
            override fun areItemsTheSame(oldItem: UserRecords, newItem: UserRecords): Boolean {

                // Logica per determinare se due elementi rappresentano lo stesso oggetto
                return oldItem.steps.stepId == newItem.steps.stepId
            }

            override fun areContentsTheSame(oldItem: UserRecords, newItem: UserRecords): Boolean {
                // Confronta se il contenuto di due oggetti è lo stesso
                // Potresti confrontare ogni campo o fare un confronto basato su una versione hash
                return oldItem.steps.date == newItem.steps.date
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.show_records, parent, false)

        return RecordsViewHolder(view, recordsViewModel)
    }



    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val dailyRecord = getItem(position)

        val dateOfRecords= dailyRecord.steps.date.toString()
        val countSteps= dailyRecord.steps.count.toString()
        val countCalories= dailyRecord.calories.count.toString()
        val countDistance= dailyRecord.distance.count.toString()
        holder.bind(dateOfRecords, countSteps, countCalories, countDistance)

    }

    class RecordsViewHolder(itemView: View, private val recordsViewModel: RecordsViewModel) : RecyclerView.ViewHolder(itemView) {

        val dateOfRecords: TextView = itemView.findViewById(R.id.DateOfRecords)

        val steps : TextView = itemView.findViewById(R.id.countSteps)
        val progressBarSteps: ProgressBar = itemView.findViewById(R.id.progressbarSteps)
        //val stepsImage : ImageView = itemView.findViewById(R.id.imageSteps)

        val calories = itemView.findViewById<TextView>(R.id.countCalories)
        val progressBarCalories: ProgressBar = itemView.findViewById(R.id.progressbarCalories)
        //val caloriesImage : ImageView = itemView.findViewById(R.id.imageCalories)

        val distance = itemView.findViewById<TextView>(R.id.countDistance)
        val progressBarDistance: ProgressBar = itemView.findViewById(R.id.progressbarDistance)
        //val distanceImage : ImageView = itemView.findViewById(R.id.imageDistance)
        fun bind(date: String, countSteps: String, countCalories: String, countDistance: String ) {

            dateOfRecords.text = date

            steps.text = countSteps
            calories.text = countCalories
            distance.text = countDistance

            // todo mettere sempre utente generalizzato in caso
            val currentGoal = recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {
                progressBarSteps.progress = Helpers.calculatePercentage(countSteps.toInt(),currentGoal.steps.toDouble() )
                progressBarCalories.progress = Helpers.calculatePercentage(countSteps.toInt(),currentGoal.calories.toDouble() )
                progressBarDistance.progress = Helpers.calculatePercentage(countSteps.toInt(),currentGoal.distance.toDouble())
            }


        }
    }

}


