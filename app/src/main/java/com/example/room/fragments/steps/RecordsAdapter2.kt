package com.example.room.fragments.steps


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.room.R
import com.example.room.database.RecordsViewModel
import com.example.room.database.UserRecords
import com.example.room.database.goal.Goal
import com.example.room.database.records.calories.Calories
import com.example.room.database.records.distance.Distance
import com.example.room.database.records.steps.Steps

class RecordsAdapter2(private val goal : Goal, private val stepsList: List<Steps>, private val caloriesList: List<Calories>, private val distanceList: List<Distance>) : RecyclerView.Adapter<RecordsAdapter2.RecordsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_records, parent, false)

        return RecordsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stepsList.size
    }


    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val dailySteps = stepsList[position]

        val dateOfRecords= Helpers.formatDateToString(dailySteps.date)
        val countSteps= dailySteps.count.toString()

        val dailyCalories = caloriesList[position]
        val countCalories = dailyCalories.count.toString()

        val dailyDistance = distanceList[position]
        val countDistance = dailyDistance.count.toString()

        holder.bind(dateOfRecords, countSteps, countCalories,countDistance, goal)

    }

    class RecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dateOfRecords: TextView = itemView.findViewById(R.id.DateOfRecords)

        private val steps : TextView = itemView.findViewById(R.id.countSteps)
        private val progressBarSteps: ProgressBar = itemView.findViewById(R.id.progressbarSteps)

        private val calories : TextView = itemView.findViewById(R.id.countCalories)
        private val progressBarCalories : ProgressBar = itemView.findViewById(R.id.progressbarCalories)

        private val distance : TextView = itemView.findViewById(R.id.countDistance)
        private val progressBarDistance : ProgressBar = itemView.findViewById(R.id.progressbarDistance)

        fun bind(date: String, countSteps: String, countCalories: String, countDistance:String, currentGoal: Goal) {

            dateOfRecords.text = date
            steps.text = countSteps
            calories.text = countCalories
            distance.text = countDistance

            progressBarSteps.progress =
                Helpers.calculatePercentage(countSteps.toDouble(), currentGoal.steps.toDouble())
            progressBarCalories.progress =
                Helpers.calculatePercentage(countCalories.toDouble(), currentGoal.steps.toDouble())
            progressBarDistance.progress =
                Helpers.calculatePercentage(countDistance.toDouble(), currentGoal.steps.toDouble())

        }
    }

}


