package it.unipd.footbyfoot.fragments.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.workout.Distance
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

class WeeklySummariesFragment : Fragment() {

    companion object {
        const val selectedItemKey = "selectedItem"
    }

    //Day of the week selected
    private var selectedItem = -1

    //Lists
    private var distanceList : List<Distance> = listOf()
    private var goalsList : List<Goal> = listOf()
    private var infoList : List<UserInfo> = listOf()

    //Views
    private lateinit var listProgressBar:  List<ProgressBar>
    private lateinit var listSteps:  List<TextView>
    private lateinit var listDay:  List<TextView>
    private lateinit var listRelative:  List<RelativeLayout>
    private lateinit var dateView:  TextView

    private lateinit var circularProgressBarSteps : ProgressBar
    private lateinit var circularProgressBarCalories : ProgressBar
    private lateinit var circularProgressBarDistance : ProgressBar

    private lateinit var circularCountSteps: TextView
    private lateinit var circularCountDistance: TextView
    private lateinit var circularCountCalories: TextView

    private lateinit var circularStepsGoal: TextView
    private lateinit var circularDistanceGoal: TextView
    private lateinit var circularCaloriesGoal: TextView

    //Personalized trace
    private val weekTrace = Firebase.performance.newTrace(RecordsApplication.weekSummaryTrace)

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

        //Lists of name of day
        listDay = listOf(
            view.findViewById(R.id.Mon),
            view.findViewById(R.id.Tue),
            view.findViewById(R.id.Wed),
            view.findViewById(R.id.Thu),
            view.findViewById(R.id.Fri),
            view.findViewById(R.id.Sat),
            view.findViewById(R.id.Sun)
        )

        //List of relative layouts (to set the listeners)
        listRelative = listOf(
            view.findViewById(R.id.RelativelayoutMon),
            view.findViewById(R.id.RelativelayoutTue),
            view.findViewById(R.id.RelativelayoutWed),
            view.findViewById(R.id.RelativelayoutThu),
            view.findViewById(R.id.RelativelayoutFri),
            view.findViewById(R.id.RelativelayoutSat),
            view.findViewById(R.id.RelativelayoutSun)
        )

        //Date
        dateView = view.findViewById(R.id.Date)

        //Progress bars and views of bottom part
        circularProgressBarSteps = view.findViewById(R.id.progressbarStepsSeclected)
        circularProgressBarCalories = view.findViewById(R.id.progressbarCaloriesSelected)
        circularProgressBarDistance = view.findViewById(R.id.progressbarDistanceSelected)

        circularCountSteps = view.findViewById(R.id.countStepsSelected)
        circularCountDistance = view.findViewById(R.id.countDistanceSelected)
        circularCountCalories = view.findViewById(R.id.countCaloriesSelected)

        circularStepsGoal = view.findViewById(R.id.stepsGoalSelected)
        circularDistanceGoal = view.findViewById(R.id.distanceGoalSelected)
        circularCaloriesGoal = view.findViewById(R.id.caloriesGoalSelected)

        //Set default progress
        circularProgressBarSteps.progress = 0
        circularProgressBarCalories.progress = 0
        circularProgressBarDistance.progress = 0

        if (savedInstanceState != null) {
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
        //Observe the info
        (activity as MainActivity).recordsViewModel.allInfo.observe(viewLifecycleOwner) { info ->
            updateInfo(info)
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Put only the selected item
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

    private fun updateInfo(info: List<UserInfo>) {
        infoList = info
        setViews()
    }

    private fun setViews() {

        //For every date in the current week (1 is Monday, 7 is Sunday, as in LocalDate)
        for (i in 1..7) {

            //Date of that day
            val date = LocalDate.now()
                .minusDays((LocalDate.now().dayOfWeek.value - i).toLong())

            //Distance and goal of that day
            val meters = Helpers.getDistanceMetersOfDate(distanceList, date)
            val goal = Helpers.getGoalOfDate(goalsList, date)
            val info = Helpers.getInfoOfDate(infoList, date)

            //Calculate the count of steps and calories
            val countS = Helpers.calculateSteps(info.height, meters)
            val countC = Helpers.calculateCalories(info.weight, meters)

            //The index of the list is i-1 as it starts from 0
            //Set the text and the progress of the bar
            listSteps[i - 1].text = countS.toString()
            listProgressBar[i - 1].progress = Helpers.calculatePercentage(
                countS.toDouble(),
                goal.steps.toDouble()
            )

            //Set the listener
            listRelative[i - 1].setOnClickListener {

                //Select item
                selectItem(i-1)

                //Set text of date and count
                dateView.text = Helpers.formatDateToString(requireActivity(),date)
                circularCountDistance.text = meters.toString()
                circularCountSteps.text = countS.toString()
                circularCountCalories.text = countC.toString()
                
                circularStepsGoal.text = goal.steps.toString()
                circularDistanceGoal.text = goal.distance.toString()
                circularCaloriesGoal.text = goal.calories.toString()

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

        //Select current date or the one previously selected
        if (selectedItem != -1) {
            listRelative[selectedItem].performClick()
        } else {
            listRelative[LocalDate.now().dayOfWeek.value-1].performClick()
        }
    }

    private fun selectItem(index: Int) {
        selectedItem = index

        //Clear all other colors
        for (i in 0..6) {
            listDay[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.OnSurface))
            listSteps[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.OnSurface))
        }

        //Set the selected one
        listDay[index].setTextColor(ContextCompat.getColor(requireContext(), R.color.progressBarGreen))
        listSteps[index].setTextColor(ContextCompat.getColor(requireContext(), R.color.progressBarGreen))
    }


    override fun onResume() {
        super.onResume()
        //Start trace
        weekTrace.start()
    }

    override fun onPause(){
        //Stop trace
        weekTrace.stop()
        super.onPause()
    }

}