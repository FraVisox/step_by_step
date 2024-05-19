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

class TodayStepsFragment : Fragment() {

    private lateinit var progressBarSteps  : ProgressBar
    private lateinit var countSteps: TextView

    private lateinit var progressBarColaries : ProgressBar
    private lateinit var countCalories : TextView

    private lateinit var progressBarDistance : ProgressBar
    private lateinit var countDistance : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        progressBarSteps = view.findViewById<ProgressBar>(R.id.progressbarStepsToday)
        countSteps = view.findViewById<TextView>(R.id.countStepsToday)

        progressBarColaries = view.findViewById<ProgressBar>(R.id.progressbarCaloriesToday)
        countCalories = view.findViewById<TextView>(R.id.countCaloriesToday)

        progressBarDistance = view.findViewById<ProgressBar>(R.id.progressbarDistanceToday)
        countDistance = view.findViewById<TextView>(R.id.countDistanceToday)

        Log.d("RecordsRoomDatabase", "todaystepsfragment")


        (activity as MainActivity).recordsViewModel.monthlyUserActivities.observe(viewLifecycleOwner, Observer { records ->

            records?.let {
                // in realta qui è solo uno
                records.forEach {
                    val countS = it.steps.count.toString()
                    val countD = it.distance.count.toString()
                    val countC = it.calories.count.toString()

                    // bisona mettere quelli selezionati dal bro
                    val stepsGoal : Int = 8004
                    val distanceGoal : Int = 1204
                    val caloriesGoal : Int = 1204

                    countSteps.text = countS
                    countCalories.text = countC
                    countDistance.text = countD


                    progressBarSteps.setProgress(Helpers.calculatePercentage(countS.toInt(), stepsGoal))
                    progressBarColaries.setProgress(Helpers.calculatePercentage(countC.toInt(), caloriesGoal))
                    progressBarDistance.setProgress(Helpers.calculatePercentage(countD.toInt(), distanceGoal))
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