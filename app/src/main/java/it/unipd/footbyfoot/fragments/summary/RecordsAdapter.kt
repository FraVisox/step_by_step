package it.unipd.footbyfoot.fragments.summary


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.workout.Distance
import java.time.LocalDate

class RecordsAdapter(private val height: Int, private val weight: Int) : RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder>() {

    private var goals : List<Goal> = listOf()

    private var distanceList: List<Distance> = listOf()

    fun submitDistances(dist: List<Distance>) {
        distanceList = dist
    }

    fun submitGoals(goalsList: List<Goal>) {
        goals = goalsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.summary_card_item, parent, false)
        return RecordsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return distanceList.size
    }


    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        //Get the distance
        val dailyDistance = distanceList[position]

        //Get the date
        val date = LocalDate.ofYearDay(dailyDistance.year, dailyDistance.dayOfYear)

        //Pass parameters
        holder.bind(
            Helpers.formatDateToString(date),
            Helpers.calculateSteps(height, dailyDistance.meters),
            Helpers.calculateCalories(weight, dailyDistance.meters),
            Helpers.distanceToKm(dailyDistance.meters),
            Helpers.getGoalOfDate(goals, date)
        )

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


