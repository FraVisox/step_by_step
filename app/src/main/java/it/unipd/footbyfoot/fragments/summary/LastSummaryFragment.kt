package it.unipd.footbyfoot.fragments.summary

import android.content.Context
import android.os.Bundle
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

class LastSummaryFragment : Fragment() {

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

        val view = inflater.inflate(R.layout.fragment_last_summaries, container, false)

        //Get Views
        progressBarSteps = view.findViewById(R.id.progressbarLastSteps)
        countSteps = view.findViewById(R.id.countLastSteps)
        goalsSteps = view.findViewById(R.id.goalsLastSteps)
        progressBarCalories = view.findViewById(R.id.progressbarLastCalories)
        countCalories = view.findViewById(R.id.countLastCalories)
        goalsCalories= view.findViewById(R.id.goalsLastCalories)
        progressBarDistance = view.findViewById(R.id.progressbarLastDistance)
        countDistance = view.findViewById(R.id.countLastDistance)
        goalsDistance = view.findViewById(R.id.goalsLastDistance)

        //Observe the distances of last week
        (activity as MainActivity).recordsViewModel.lastWeekDistances.observe(viewLifecycleOwner) { distanceList ->
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
        if (distanceList.isEmpty() || goalsList.isEmpty()) { //TODO: come facciamo a mettere un goal di default?
            return
        }


        //Get height and weight
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val weight = preferences.getInt(SettingsFragment.WEIGHT, SettingsFragment.defaultWeight)
        val height = preferences.getInt(SettingsFragment.HEIGHT, SettingsFragment.defaultHeight)

        val currentGoal = goalsList.first()

        val countD = distanceList.first().meters.toString()
        countDistance.text = countD

        val countS = Helpers.calculateSteps(height, distanceList.first().meters)
        countSteps.text = countS.toString()

        val countC = Helpers.calculateCalories(weight, distanceList.first().meters)
        countCalories.text = countC.toString()

        goalsCalories.text = currentGoal.calories.toString()
        goalsSteps.text = currentGoal.steps.toString()
        goalsDistance.text = currentGoal.distance.toString()

        progressBarDistance.progress = Helpers.calculatePercentage(
            Helpers.distanceToKm(distanceList.first().meters),
            currentGoal.distance
        )
        progressBarCalories.progress =
            Helpers.calculatePercentage(countC, currentGoal.calories.toDouble())
        progressBarSteps.progress =
            Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
    }

}
