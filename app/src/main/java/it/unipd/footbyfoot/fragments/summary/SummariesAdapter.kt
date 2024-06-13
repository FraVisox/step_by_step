package it.unipd.footbyfoot.fragments.summary

import android.app.Activity
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
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.workout.Distance
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

//Adapter used in AllSummariesFragment
class SummariesAdapter(private val activity: Activity) : ListAdapter<Distance, SummariesAdapter.RecordsViewHolder>(DISTANCE_COMPARATOR) {

    //ListAdapters need a comparator
    companion object {
        private val DISTANCE_COMPARATOR = object : DiffUtil.ItemCallback<Distance>() {
            //Tells if two items are the same
            override fun areItemsTheSame(oldItem: Distance, newItem: Distance): Boolean {
                return (oldItem.year == newItem.year) && (oldItem.dayOfYear == newItem.dayOfYear)
            }

            //Tells if two items have the same content
            override fun areContentsTheSame(oldItem: Distance, newItem: Distance): Boolean {
                return oldItem.meters == newItem.meters && areItemsTheSame(oldItem, newItem)
            }
        }
    }

    //List of goals and info
    private var goals : List<Goal> = listOf()
    private var info : List<UserInfo> = listOf()

    fun submitGoals(goalsList: List<Goal>) {
        goals = goalsList
        //The only record that can be affected immediately by the change of goals is today's record, on the first position
        notifyItemChanged(0)
    }

    fun submitInfo(infoList: List<UserInfo>) {
        info = infoList
        //The only record that can be affected immediately by the change of info is today's record, on the first position
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_summary, parent, false)
        return RecordsViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        //Get the distance
        val dailyDistance = getItem(position)

        //Get the date
        val date = LocalDate.ofYearDay(dailyDistance.year, dailyDistance.dayOfYear)

        //Get info
        val info = Helpers.getInfoOfDate(info, date)

        //Pass parameters
        holder.bind(
            Helpers.formatDateToString(activity, date),
            Helpers.calculateSteps(info.height, dailyDistance.meters),
            Helpers.calculateCalories(info.weight, dailyDistance.meters),
            dailyDistance.meters,
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

        fun bind(date: String, countSteps: Int, countCalories: Double, countDistance: Int, currentGoal: Goal) {

            //Set views
            dateOfRecords.text = date

            steps.text = countSteps.toString()
            calories.text = countCalories.toString()
            distance.text = countDistance.toString()

            stepsGoal.text = currentGoal.steps.toString()
            caloriesGoal.text = currentGoal.calories.toString()
            distanceGoal.text = currentGoal.distance.toString()

            progressBarSteps.progress = Helpers.calculatePercentage(countSteps.toDouble(), currentGoal.steps.toDouble())
            progressBarCalories.progress = Helpers.calculatePercentage(countCalories, currentGoal.calories.toDouble())
            progressBarDistance.progress = Helpers.calculatePercentage(countDistance.toDouble(), currentGoal.distance.toDouble())

        }
    }

}


