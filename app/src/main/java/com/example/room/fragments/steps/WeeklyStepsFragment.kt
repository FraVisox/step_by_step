package com.example.room.fragments.steps

import android.os.Bundle
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
import com.example.room.database.RecordsViewModelFactory

class WeeklyStepsFragment : Fragment() {

    private val recordsViewModel: RecordsViewModel by viewModels {
        RecordsViewModelFactory((requireActivity().application as RecordsApplication).repository)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_weekly_steps, container, false)

        val progressBarStepsMon = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekMon)
        val progressBarStepsTues = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekTues)
        val progressBarStepsWed = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekWed)
        val progressBarStepsThur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekThurs)
        val progressBarStepsFri = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekFri)
        val progressBarStepsSatur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSatur)
        val progressBarStepsSun = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSun)

        val countStepsMon = view.findViewById<TextView>(R.id.stepsMon)
        val countStepsTues = view.findViewById<TextView>(R.id.stepsTues)
        val countStepsWed = view.findViewById<TextView>(R.id.stepsWed)
        val countStepsThur = view.findViewById<TextView>(R.id.stepsThur)
        val countStepsFri = view.findViewById<TextView>(R.id.stepsFri)
        val countStepsSatur = view.findViewById<TextView>(R.id.stepsSat)
        val countStepsSun = view.findViewById<TextView>(R.id.stepsSun)

        recordsViewModel.todayUserActivities.observe(viewLifecycleOwner, Observer { records ->
            records?.let {
                // In realtà qui è solo uno
                records.forEach { record ->
                    val countS = record.steps.count.toString()
                    val countD = record.distance.count.toString()
                    val countC = record.calories.count.toString()

                    // Bisogna mettere quelli selezionati dal bro
                    val stepsGoal = 8004
                    val distanceGoal = 1204
                    val caloriesGoal = 1204

                    // Determina il giorno della settimana
                    val dayOfWeek =  Helpers.getDayOfWeek(record.steps.date)

                    when (dayOfWeek) {
                        "Monday" -> {
                            countStepsMon.text = countS
                            progressBarStepsMon.progress = Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Tuesday" -> {
                            countStepsTues.text = countS
                            progressBarStepsTues.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Wednesday" -> {
                            countStepsWed.text = countS
                            progressBarStepsWed.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Thursday" -> {
                            countStepsThur.text = countS
                            progressBarStepsThur.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Friday" -> {
                            countStepsFri.text = countS
                            progressBarStepsFri.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Saturday" -> {
                            countStepsSatur.text = countS
                            progressBarStepsSatur.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Sunday" -> {
                            countStepsSun.text = countS
                            progressBarStepsSun.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                    }
                }
            }
        })

        return view
    }

}
