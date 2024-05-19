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
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.RecordsApplication
import com.example.room.database.RecordsViewModel
import com.example.room.database.RecordsViewModelFactory

class WeeklyStepsFragment : Fragment() {


    private lateinit var progressBarStepsMon : ProgressBar
    private lateinit var progressBarStepsTues : ProgressBar
    private lateinit var progressBarStepsWed : ProgressBar
    private lateinit var progressBarStepsThur : ProgressBar
    private lateinit var progressBarStepsFri : ProgressBar
    private lateinit var progressBarStepsSatur : ProgressBar
    private lateinit var progressBarStepsSun : ProgressBar

    private lateinit var countStepsMon : TextView
    private lateinit var countStepsTues : TextView
    private lateinit var countStepsWed : TextView
    private lateinit var countStepsThur : TextView
    private lateinit var countStepsFri : TextView
    private lateinit var countStepsSatur : TextView
    private lateinit var countStepsSun : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_weekly_steps, container, false)

        progressBarStepsMon = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekMon)
        progressBarStepsTues = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekTues)
        progressBarStepsWed = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekWed)
        progressBarStepsThur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekThurs)
        progressBarStepsFri = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekFri)
        progressBarStepsSatur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSatur)
        progressBarStepsSun = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSun)

        countStepsMon = view.findViewById<TextView>(R.id.stepsMon)
        countStepsTues = view.findViewById<TextView>(R.id.stepsTues)
        countStepsWed = view.findViewById<TextView>(R.id.stepsWed)
        countStepsThur = view.findViewById<TextView>(R.id.stepsThur)
        countStepsFri = view.findViewById<TextView>(R.id.stepsFri)
        countStepsSatur = view.findViewById<TextView>(R.id.stepsSat)
        countStepsSun = view.findViewById<TextView>(R.id.stepsSun)

        (activity as MainActivity).recordsViewModel.monthlyUserActivities.observe(viewLifecycleOwner, Observer{ records ->
            records?.let {

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
