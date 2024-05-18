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
import com.example.room.R
import com.example.room.RecordsApplication
import com.example.room.database.RecordsViewModel
import com.example.room.SetNewGoalsActivity
import com.example.room.database.RecordsViewModelFactory

class TodayStepsFragment : Fragment() {

    private val recordsViewModel: RecordsViewModel by viewModels {
        RecordsViewModelFactory((requireActivity().application as RecordsApplication).repository)
    }
    val progressBarSteps : ProgressBar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        val progressBarSteps = view.findViewById<ProgressBar>(R.id.progressbarStepsToday)
        val countSteps = view.findViewById<TextView>(R.id.countStepsToday)



        val progressBarColaries = view.findViewById<ProgressBar>(R.id.progressbarCaloriesToday)
        val countCalories = view.findViewById<TextView>(R.id.countCaloriesToday)

        val progressBarDistance = view.findViewById<ProgressBar>(R.id.progressbarDistanceToday)
        val countDistance = view.findViewById<TextView>(R.id.countDistanceToday)

        Log.d("RecordsRoomDatabase", "todaystepsfragment")


        recordsViewModel.todayUserActivities.observe(viewLifecycleOwner, Observer { records ->
            // Update the cached copy of the words in the adapter.
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
        val goals : TextView = view.findViewById(R.id.goalsStepsToday)
        goals.setOnClickListener {
            val intent = Intent(view.context,SetNewGoalsActivity::class.java)
            view.context.startActivity(intent)
        }



        return view
    }


}