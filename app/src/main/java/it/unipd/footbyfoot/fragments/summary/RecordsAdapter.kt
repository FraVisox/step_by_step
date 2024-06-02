package it.unipd.footbyfoot.fragments.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.workout.Distance
import java.time.LocalDate

class RecordsAdapter(private val height: Int, private val weight: Int) : ListAdapter<Distance, RecordsAdapter.RecordsViewHolder>(DISTANCE_COMPARATOR) {

    //ListAdapters need a comparator
    companion object {
        private val DISTANCE_COMPARATOR = object : DiffUtil.ItemCallback<Distance>() {
            //Tells if two items are the same
            override fun areItemsTheSame(oldItem: Distance, newItem: Distance): Boolean {
                return (oldItem.year == newItem.year) && (oldItem.dayOfYear == newItem.dayOfYear)
            }

            //Tells if two items have the same content
            override fun areContentsTheSame(oldItem: Distance, newItem: Distance): Boolean {
                return areItemsTheSame(oldItem, newItem)
            }
        }
    }

    private var goals : List<Goal> = listOf()

    fun submitGoals(goalsList: List<Goal>) {
        goals = goalsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.summary_card_item, parent, false)
        return RecordsViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        //Get the distance
        val dailyDistance = getItem(position)

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
        private val stepsGoal : TextView = itemView.findViewById(R.id.stepsGoal)

        private val calories : TextView = itemView.findViewById(R.id.countCalories)
        private val progressBarCalories : ProgressBar = itemView.findViewById(R.id.progressbarCalories)
        private val caloriesGoal : TextView = itemView.findViewById(R.id.caloriesGoal)

        private val distance : TextView = itemView.findViewById(R.id.countDistance)
        private val progressBarDistance : ProgressBar = itemView.findViewById(R.id.progressbarDistance)
        private val distanceGoal : TextView = itemView.findViewById(R.id.distanceGoal)

        fun bind(date: String, countSteps: Int, countCalories: Double, countDistance: Double, currentGoal: Goal) {

            dateOfRecords.text = date

            steps.text = countSteps.toString()
            calories.text = countCalories.toString()
            distance.text = countDistance.toString()

            stepsGoal.text = currentGoal.steps.toString()
            caloriesGoal.text = currentGoal.calories.toString()
            distanceGoal.text = currentGoal.distance.toString()

            progressBarSteps.progress = Helpers.calculatePercentage(countSteps.toDouble(), currentGoal.steps.toDouble())
            progressBarCalories.progress = Helpers.calculatePercentage(countCalories, currentGoal.calories.toDouble())
            progressBarDistance.progress = Helpers.calculatePercentage(countDistance, currentGoal.distance.toDouble())

        }
    }

}


