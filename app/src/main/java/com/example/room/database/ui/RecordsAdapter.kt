package com.example.room.database.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.room.R
import com.example.room.database.UserRecords

class RecordsAdapter : ListAdapter<UserRecords, RecordsAdapter.RecordsViewHolder>(DIFF_CALLBACK) {

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
            .inflate(R.layout.fragment_show_records, parent, false)

        return RecordsViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val dailyRecord = getItem(position)

        val dateOfRecords= dailyRecord.steps.date.toString()
        val countSteps= dailyRecord.steps.count.toString()
        val countCalories= dailyRecord.calories.count.toString()
        val countDistance= dailyRecord.distance.count.toString()
        holder.bind(dateOfRecords, countSteps, countCalories, countDistance)

    }

    class RecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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

            progressBarSteps.progress = countSteps.toInt()
            progressBarCalories.progress = countCalories.toInt()
            progressBarDistance.progress = countDistance.toInt()

        }
    }

}

/*
ok
class DailyRecordsAdapter(private val DailyRecordsList: List<UserActivityRecord>) :
    RecyclerView.Adapter<DailyRecordsAdapter.DailyRecordsViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class DailyRecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateOfRecords: TextView = itemView.findViewById(R.id.DateOfRecords)
        val steps : TextView = itemView.findViewById(R.id.StepsCard)
        val stepsImage : ImageView = itemView.findViewById(R.id.StepsCard)

        val calories = itemView.findViewById<TextView>(R.id.CaloriesCard)
        val caloriesImage = itemView.findViewById<ImageView>(R.id.CaloriesCard)

        val distance = itemView.findViewById<TextView>(R.id.DistanceCard)
        val distanceImage = itemView.findViewById<ImageView>(R.id.DistanceCard)

        fun bind(date: String, countSteps: String, countCalories: String, countDistance: String ) {
            dateOfRecords.text = date
            steps.text = countSteps
            calories.text = countCalories
            distance.text = countDistance

            stepsImage.setImageResource(R.drawable.baseline_directions_run_24)
            caloriesImage.setImageResource(R.drawable.calories)
            distanceImage.setImageResource(R.drawable.distance)

        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyRecordsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_records_item, parent, false)

        return DailyRecordsViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return DailyRecordsList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: DailyRecordsViewHolder, position: Int) {
        val dailyRecord = DailyRecordsList[position]

        val dateOfRecords= dailyRecord.steps.date.toString()
        val countSteps= dailyRecord.steps.count.toString()
        val countCalories= dailyRecord.calories.count.toString()
        val countDistance= dailyRecord.distance.count.toString()
        holder.bind(dateOfRecords, countSteps, countCalories, countDistance)

    }
}


 */