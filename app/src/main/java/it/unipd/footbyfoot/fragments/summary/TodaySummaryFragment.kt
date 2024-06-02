package it.unipd.footbyfoot.fragments.summary

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.workout.Distance
import it.unipd.footbyfoot.fragments.settings.SettingsFragment

class TodaySummaryFragment : Fragment() {

    //TODO: le distanze si dovrebbero vedere anche se i goal non ci sono
    //Lists
    private var distanceList : List<Distance> = listOf()
    private var goalsList : List<Goal> = listOf()

    //Steps
    private lateinit var progressBarSteps  : ProgressBar
    private lateinit var countSteps: TextView
    private lateinit var goalsSteps : TextView

    //Calories
    private lateinit var progressBarCalories : ProgressBar
    private lateinit var countCalories : TextView
    private lateinit var goalsCalories : TextView

    //Distances
    private lateinit var progressBarDistance : ProgressBar
    private lateinit var countDistance : TextView
    private lateinit var goalsDistance : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_today_summary, container, false)

        //Get Views
        progressBarSteps = view.findViewById(R.id.progressbarTodaySteps)
        countSteps = view.findViewById(R.id.countTodaySteps)
        goalsSteps = view.findViewById(R.id.goalsTodaySteps)

        progressBarCalories = view.findViewById(R.id.progressbarTodayCalories)
        countCalories = view.findViewById(R.id.countTodayCalories)
        goalsCalories= view.findViewById(R.id.goalsTodayCalories)

        progressBarDistance = view.findViewById(R.id.progressbarTodayDistance)
        countDistance = view.findViewById(R.id.countTodayDistance)
        goalsDistance = view.findViewById(R.id.goalsTodayDistance)

        //Observe the distances of today
        (activity as MainActivity).recordsViewModel.todayDistance.observe(viewLifecycleOwner) { distanceList ->
            updateDistances(distanceList)
        }

        //Observe the goals
        (activity as MainActivity).recordsViewModel.allGoals.observe(viewLifecycleOwner) { goals ->
            updateGoals(goals)
        }

        return view
    }

    private fun updateDistances(dist: List<Distance>) {
        distanceList = dist
        setViews()
    }

    private fun updateGoals(goals: List<Goal>) {
        goalsList = goals
        setViews()
    }

    private fun setViews() {
        //Get height and weight
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val weight = preferences.getInt(SettingsFragment.WEIGHT, SettingsFragment.defaultWeight)
        val height = preferences.getInt(SettingsFragment.HEIGHT, SettingsFragment.defaultHeight)

        //Get current goal (or null if it doesn't exist)
        val currentGoal = if (goalsList.isNotEmpty()) goalsList.first() else Helpers.defaultGoal
        //Get today distance (or zero if not present)
        val distance = if (distanceList.isNotEmpty()) distanceList.first().meters else 0

        val countD = distance.toString()
        countDistance.text = countD

        val countS = Helpers.calculateSteps(height, distance)
        countSteps.text = countS.toString()

        val countC = Helpers.calculateCalories(weight, distance)
        countCalories.text = countC.toString()

        goalsCalories.text = currentGoal.calories.toString()
        goalsSteps.text = currentGoal.steps.toString()
        goalsDistance.text = currentGoal.distance.toString()

        progressBarDistance.progress = Helpers.calculatePercentage(
            Helpers.distanceToKm(distance),
            currentGoal.distance.toDouble()
        )
        progressBarCalories.progress =
            Helpers.calculatePercentage(countC, currentGoal.calories.toDouble())
        progressBarSteps.progress =
            Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
    }

}
