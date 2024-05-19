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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        progressBarSteps = view.findViewById<ProgressBar>(R.id.progressbarStepsToday)
        countSteps = view.findViewById<TextView>(R.id.countStepsToday)

        progressBarCalories = view.findViewById<ProgressBar>(R.id.progressbarCaloriesToday)
        countCalories = view.findViewById<TextView>(R.id.countCaloriesToday)

        progressBarDistance = view.findViewById<ProgressBar>(R.id.progressbarDistanceToday)
        countDistance = view.findViewById<TextView>(R.id.countDistanceToday)

        Log.d("RecordsRoomDatabase", "todaystepsfragment")

/*
        (activity as MainActivity).recordsViewModel.todayUserActivities.observe(viewLifecycleOwner, Observer { records ->

            records?.let {
                // in realta qui è solo uno
                records.forEach {

                    val countS = it.steps.count.toString()
                    val countD = it.distance.count.toString()
                    val countC = it.calories.count.toString()

                    countSteps.text = countS
                    countCalories.text = countC
                    countDistance.text = countD

                    // Todo: uso utente 1 da generalizzare in caso
                    val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                    goalsSteps.text = currentGoal?.steps.toString()
                    goalsCalories.text = currentGoal?.calories.toString()
                    goalsDistance.text = currentGoal?.distance.toString()

                    // non è sicuramente null in teoria nel nostro caso perche lo pololiamo noi l'utente
                    if (currentGoal != null) {
                        progressBarSteps.progress = Helpers.calculatePercentage(countS.toInt(), currentGoal.steps)
                        progressBarCalories.progress = Helpers.calculatePercentage(countC.toInt(), currentGoal.calories)
                        progressBarDistance.progress = Helpers.calculatePercentage(countD.toInt(), currentGoal.distance.toInt())
                    }

                }
            }
        })


 */
        (activity as MainActivity).recordsViewModel.insertSteps(Steps(1, 1, 1000, Date()))
        (activity as MainActivity).recordsViewModel.insertCalories(Calories(1, 1, 500, Date()))
        (activity as MainActivity).recordsViewModel.insertDistance(Distance(1, 1, 3.5, Date()))
        (activity as MainActivity).recordsViewModel.insertGoal(Goal(1, 1000, 1000, 100.1))

        (activity as MainActivity).recordsViewModel.todaySteps.observe(viewLifecycleOwner, Observer { stepsList ->
            if (!stepsList.isNullOrEmpty()) {
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

        (activity as MainActivity).recordsViewModel.todaySteps.observe(viewLifecycleOwner, Observer { steps ->

            // in realta qui è solo uno
            steps.forEach {
                val countS = it.count.toString()
                countSteps.text = countS
                Log.d("oggi", "countS: $countS")

                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                goalsSteps.text = currentGoal?.steps.toString()

                // non è sicuramente null in teoria nel nostro caso perche lo pololiamo noi l'utente
                if (currentGoal != null) {
                    progressBarSteps.progress = Helpers.calculatePercentage(countS.toInt(), currentGoal.steps)

                }
            }
        })

        (activity as MainActivity).recordsViewModel.todayCalories.observe(viewLifecycleOwner, Observer { calories ->

            calories.forEach {

                val countC = it.count.toString()
                countCalories.text = countC


                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                goalsCalories.text = currentGoal?.calories.toString()

                if (currentGoal != null) {
                    progressBarCalories.progress = Helpers.calculatePercentage(countC.toInt(), currentGoal.calories)
                }
            }
        })

        (activity as MainActivity).recordsViewModel.todayDistance.observe(viewLifecycleOwner, Observer { distance ->

            distance.forEach {

                val countD = it.count.toString()
                countDistance.text = countD

                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

                goalsDistance.text = currentGoal?.distance.toString()

                if (currentGoal != null) {
                    progressBarDistance.progress = Helpers.calculatePercentage(countD.toInt(), currentGoal.distance.toInt())
                }
            }
        })



        /*
        progressBarSteps.setProgress(80)
        countSteps.setText("120")

        progressBarColaries.setProgress(20)
        countCalories.setText("660")

        progressBarDistance.setProgress(90)
        countDistance.setText("5")
        */




        return view
    }


}

/*
 val goals : TextView = view.findViewById(R.id.goalsStepsToday)
        goals.setOnClickListener {
            val intent = Intent(view.context,SetNewGoalsActivity::class.java)
            view.context.startActivity(intent)
        }
 */