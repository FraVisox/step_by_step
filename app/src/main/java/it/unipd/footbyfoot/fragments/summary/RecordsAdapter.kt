package it.unipd.footbyfoot.fragments.summary


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.records.distance.Distance
import it.unipd.footbyfoot.database.user.User

class RecordsAdapter(private val goal : Goal, private val user : User, private val distanceList: List<Distance>) : RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.summary_card_item, parent, false)

        return RecordsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return distanceList.size
    }


    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {

        val dailyDistance = distanceList[position]
        val dateOfRecords= Helpers.formatDateToString(dailyDistance.date)

        val countDistance = dailyDistance.count
        val countSteps = Helpers.calculateSteps(user.height.toDouble(), dailyDistance.count)
        val countCalories = Helpers.calculateCalories(user.weight.toDouble(), dailyDistance.count)

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

        fun bind(date: String, countSteps: Int, countCalories: Double, countDistance: Double, currentGoal: Goal) {

            dateOfRecords.text = date
            steps.text = countSteps.toString()
            calories.text = countCalories.toString()
            distance.text = countDistance.toString()

            progressBarSteps.progress = Helpers.calculatePercentage(countSteps.toDouble(), currentGoal.steps.toDouble())
            progressBarCalories.progress = Helpers.calculatePercentage(countCalories, currentGoal.calories.toDouble())
            progressBarDistance.progress = Helpers.calculatePercentage(countDistance, currentGoal.distance)

        }
    }

}


