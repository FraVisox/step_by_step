package it.unipd.footbyfoot.fragments.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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

class TodaySummaryFragment : Fragment() {

    //Lists of data of database
    private var distance : Distance? = null
    private var goalsList : List<Goal> = listOf()
    private var infoList : List<UserInfo> = listOf()

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

    //Personalized trace
    private val dayTrace = Firebase.performance.newTrace(RecordsApplication.todaySummaryTrace)

    private val date = LocalDate.now()

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

        //Observe the goals
        (activity as MainActivity).recordsViewModel.allGoals.observe(viewLifecycleOwner) { goals ->
            updateGoals(goals)
        }

        //Observe the info
        (activity as MainActivity).recordsViewModel.allInfo.observe(viewLifecycleOwner) { info ->
            updateInfo(info)
        }

        //Observe the distances
        (activity as MainActivity).recordsViewModel.getTodayDistance().observe(viewLifecycleOwner) { distance ->
            updateDistances(distance)
        }

        return view
    }

    private fun updateDistances(dist: Distance) {
        distance = dist
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
        //Get height and weight
        val info = Helpers.getInfoOfDate(infoList, LocalDate.now())
        val weight = info.weight
        val height = info.height

        //Get current goal (or null if it doesn't exist)
        val currentGoal = if (goalsList.isNotEmpty()) goalsList.first() else Helpers.defaultGoal

        //Get today distance (or zero if not present)
        val distance = distance?.meters ?: 0

        //Get count of steps, distance and calories
        val countD = distance.toString()
        countDistance.text = countD

        val countS = Helpers.calculateSteps(height, distance)
        countSteps.text = countS.toString()

        val countC = Helpers.calculateCalories(weight, distance)
        countCalories.text = countC.toString()

        //Set goals
        goalsCalories.text = currentGoal.calories.toString()
        goalsSteps.text = currentGoal.steps.toString()
        goalsDistance.text = currentGoal.distance.toString()

        //Set progress
        progressBarDistance.progress = Helpers.calculatePercentage(
            distance.toDouble(),
            currentGoal.distance.toDouble()
        )
        progressBarCalories.progress = Helpers.calculatePercentage(
            countC,
            currentGoal.calories.toDouble()
        )
        progressBarSteps.progress = Helpers.calculatePercentage(
            countS.toDouble(),
            currentGoal.steps.toDouble()
        )
    }

    override fun onResume() {
        super.onResume()

        if (LocalDate.now() != date) {
            //Observe the distances of today: it is done in onResume as the date could change while the user is
            //in the app: if the date changes, when this fragment is redisplayed, it will be updated
            (activity as MainActivity).recordsViewModel.getTodayDistance()
                .observe(viewLifecycleOwner) { distance ->
                    updateDistances(distance)
                }
        }

        //Start trace
        dayTrace.start()
    }
    override fun onPause(){
        //Stop trace
        dayTrace.stop()
        super.onPause()
    }

}