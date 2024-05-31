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
import java.time.LocalDate

class WeeklySummariesFragment : Fragment() {

    companion object {
        const val selectedItemKey = "selectedItem"
    }

    private var selectedItem = -1

    private var distanceList : List<Distance> = listOf()
    private var goalsList : List<Goal> = listOf()

    private lateinit var listProgressBar:  List<ProgressBar>
    private lateinit var listSteps:  List<TextView>
    private lateinit var dateView:  TextView

    private lateinit var circularProgressBarSteps : ProgressBar
    private lateinit var circularProgressBarCalories : ProgressBar
    private lateinit var circularProgressBarDistance : ProgressBar

    private lateinit var circularCountSteps: TextView
    private lateinit var circularCountDistance: TextView
    private lateinit var circularCountCalories: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weekly_summaries, container, false)

        //List of progress bars
        listProgressBar = listOf(
            view.findViewById(R.id.progressbarStepsMon),
            view.findViewById(R.id.progressbarStepsTue),
            view.findViewById(R.id.progressbarStepsWed),
            view.findViewById(R.id.progressbarStepsThu),
            view.findViewById(R.id.progressbarStepsFri),
            view.findViewById(R.id.progressbarStepsSat),
            view.findViewById(R.id.progressbarStepsSun)
        )

        //List of number of steps
        listSteps= listOf(
            view.findViewById(R.id.stepsMon),
            view.findViewById(R.id.stepsTue),
            view.findViewById(R.id.stepsWed),
            view.findViewById(R.id.stepsThu),
            view.findViewById(R.id.stepsFri),
            view.findViewById(R.id.stepsSat),
            view.findViewById(R.id.stepsSun)
        )

        //Date
        dateView = view.findViewById(R.id.Date)

        //Progress bars and views of bottom part
        circularProgressBarSteps = view.findViewById(R.id.progressbarTodaySteps)
        circularProgressBarCalories = view.findViewById(R.id.progressbarTodayCalories)
        circularProgressBarDistance = view.findViewById(R.id.progressbarLastDistance)

        circularProgressBarSteps.progress = 0
        circularProgressBarCalories.progress = 0
        circularProgressBarDistance.progress = 0

        circularCountSteps = view.findViewById(R.id.countTodaySteps)
        circularCountDistance = view.findViewById(R.id.countLastDistance)
        circularCountCalories = view.findViewById(R.id.countTodayCalories)

        if (savedInstanceState != null) {
            Log.d("AAA", selectedItem.toString())
            selectedItem = savedInstanceState.getInt(selectedItemKey)
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(selectedItemKey, selectedItem)
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

        //For every date in the current week (1 is Monday, 7 is Sunday, as in LocalDate)
        for (i in 1..7) {

            //Date of that day
            val date = LocalDate.now()
                .minusDays((LocalDate.now().dayOfWeek.value - i).toLong())

            //Distance and goal of that day
            val meters = Helpers.getDistanceMetersOfDate(distanceList, date)
            val goal = Helpers.getGoalOfDate(goalsList, date)

            //Calculate the count of steps and calories
            val countS = Helpers.calculateSteps(height, meters)
            val countC = Helpers.calculateCalories(weight, meters)

            //The index of the list is i-1 as it starts from 0
            //Set the text and the progress of the bar
            listSteps[i - 1].text = countS.toString()
            listProgressBar[i - 1].progress = Helpers.calculatePercentage(
                countS.toDouble(),
                goal.steps.toDouble()
            )

            //Set the listener
            listProgressBar[i - 1].setOnClickListener {

                selectedItem = i-1

                //Set text of date and count
                dateView.text = Helpers.formatDateToString(date)
                circularCountDistance.text = meters.toString()
                circularCountSteps.text = countS.toString()
                circularCountCalories.text = countC.toString()

                circularProgressBarDistance.progress = Helpers.calculatePercentage(
                    meters.toDouble(),
                    goal.distance.toDouble()
                )
                circularProgressBarCalories.progress = Helpers.calculatePercentage(
                    countC,
                    goal.calories.toDouble()
                )
                circularProgressBarSteps.progress = listProgressBar[i - 1].progress
            }
        }

        Log.d("AAA", "click on $selectedItem")
        if (selectedItem != -1) {
            Log.d("AAA", "yyeeee click on $selectedItem")

            listProgressBar[selectedItem].performClick()
        }
    }

}