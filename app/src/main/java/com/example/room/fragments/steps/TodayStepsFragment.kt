package com.example.room.fragments.steps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.RecordsApplication
import com.example.room.database.RecordsViewModel
import com.example.room.database.RecordsViewModelFactory
import com.example.room.database.goal.Goal
import com.example.room.database.records.calories.Calories
import com.example.room.database.records.distance.Distance
import com.example.room.database.records.steps.Steps
import java.util.Date

class TodayStepsFragment : Fragment() {

    private lateinit var progressBarSteps  : ProgressBar
    private lateinit var countSteps: TextView
    private lateinit var goalsSteps : TextView

    private lateinit var progressBarCalories : ProgressBar
    private lateinit var countCalories : TextView
    private lateinit var goalsCalories : TextView

    private lateinit var progressBarDistance : ProgressBar
    private lateinit var countDistance : TextView
    private lateinit var goalsDistance : TextView
     var ListGoals : List<Goal>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        progressBarSteps = view.findViewById<ProgressBar>(R.id.progressbarStepsToday)
        countSteps = view.findViewById<TextView>(R.id.countStepsToday)
        goalsSteps = view.findViewById(R.id.goalsStepsToday)
        progressBarCalories = view.findViewById<ProgressBar>(R.id.progressbarCaloriesToday)
        countCalories = view.findViewById<TextView>(R.id.countCaloriesToday)
        goalsCalories= view.findViewById(R.id.goalsCaloriesToday)
        progressBarDistance = view.findViewById<ProgressBar>(R.id.progressbarDistanceToday)
        countDistance = view.findViewById<TextView>(R.id.countDistanceToday)
        goalsDistance = view.findViewById(R.id.goalsDistanceToday)
        Log.d("RecordsRoomDatabase", "todaystepsfragment")


        (activity as MainActivity).recordsViewModel.todaySteps.observe(viewLifecycleOwner, Observer { stepsList ->
            if (stepsList.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                val stepsString = stepsList.joinToString(separator = "\n") { step ->
                    " UserID: ${step.userId}, Steps: ${step.count}, Date: ${step.date}"
                }
                // Logga il contenuto della lista
                Log.d("oggi", stepsString)
            } else {
                Log.d("oggi", "La lista dei passi di oggi è vuota")
            }
        })

        // todo bug enorme se lo tolgo non va niente !!
        (activity as MainActivity).recordsViewModel.userGoal.observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                val goalsString = goals.joinToString(separator = "\n") { goals ->
                    " UserID: ${goals.userId}, Steps: ${goals.steps}, Date: ${goals.distance}"
                }
                // Logga il contenuto della lista
                Log.d("oggi", goalsString)
            } else {
                Log.d("oggi", "La lista dei passi di oggi è vuota")
            }
        })
        (activity as MainActivity).recordsViewModel.users.observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                val goalsString = goals.joinToString(separator = "\n") { goals ->
                    "OK"
                }
                // Logga il contenuto della lista
                Log.d("oggi", goalsString)
            } else {
                Log.d("oggi", "La lista dei Users di oggi è vuota")
            }
        })
/*

        (activity as MainActivity).recordsViewModel.todaySteps.observe(viewLifecycleOwner, Observer { steps ->

            if (steps.isNotEmpty() && steps.size == 1) {

                val countS = steps.last().count.toString()
                countSteps.text = countS

                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                goalsSteps.text = currentGoal?.steps.toString()

                if (currentGoal?.steps != null) {
                    progressBarSteps.progress = Helpers.calculatePercentage(steps.last().count.toDouble(), currentGoal.steps.toDouble())
                }
            }
        })

        (activity as MainActivity).recordsViewModel.todayCalories.observe(viewLifecycleOwner, Observer { calories ->

            if (calories.isNotEmpty() && calories.size == 1) {

                val countC = calories.last().count.toString()
                countCalories.text = countC


                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                goalsCalories.text = currentGoal?.calories.toString()

                if (currentGoal?.calories != null) {
                    progressBarCalories.progress = Helpers.calculatePercentage(calories.last().count.toDouble(), currentGoal.calories.toDouble())
                }
            }
        })
*/
        (activity as MainActivity).recordsViewModel.todayDistance.observe(viewLifecycleOwner, Observer { distance ->

            if (distance.isNotEmpty() && distance.size == 1) {
                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                val currentUser = (activity as MainActivity).recordsViewModel.users.value?.find { it.userId == 1 }
                val countD = distance.last().count.toString()

                countDistance.text = countD
                if(currentUser != null && currentGoal != null){

                    val countS = Helpers.calculateSteps(currentUser.height, distance.last().count)
                    countSteps.text = countS.toString()

                    val countC = Helpers.calculateCalories(distance.last().count,currentUser.weight)
                    countCalories.text = countC.toString()

                    goalsCalories.text = currentGoal.calories.toString()
                    goalsSteps.text = currentGoal.steps.toString()
                    goalsDistance.text = currentGoal.distance.toString()

                    progressBarDistance.progress = Helpers.calculatePercentage(distance.last().count.toDouble(), currentGoal.distance.toDouble())
                    progressBarCalories.progress = Helpers.calculatePercentage(countC, currentGoal.calories.toDouble())
                    progressBarSteps.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())


                }

            }

        })

        return view
    }


}
