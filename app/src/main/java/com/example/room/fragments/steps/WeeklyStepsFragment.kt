package com.example.room.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.room.MainActivity
import com.example.room.R
import java.lang.RuntimeException
import java.util.Calendar

class WeeklyStepsFragment : Fragment() {


    private lateinit var progressBarStepsMon: ProgressBar
    private lateinit var progressBarStepsTues: ProgressBar
    private lateinit var progressBarStepsWed: ProgressBar
    private lateinit var progressBarStepsThur: ProgressBar
    private lateinit var progressBarStepsFri: ProgressBar
    private lateinit var progressBarStepsSatur: ProgressBar
    private lateinit var progressBarStepsSun: ProgressBar

    private lateinit var countStepsMon: TextView
    private lateinit var countStepsTues: TextView
    private lateinit var countStepsWed: TextView
    private lateinit var countStepsThur: TextView
    private lateinit var countStepsFri: TextView
    private lateinit var countStepsSatur: TextView
    private lateinit var countStepsSun: TextView


    private lateinit var CircularProgressBarSteps: ProgressBar
    private lateinit var CircularProgressBarCalories: ProgressBar
    private lateinit var CircularProgressBarDistance: ProgressBar

    private lateinit var CircularCountSteps: TextView
    private lateinit var CircularCountDistance: TextView
    private lateinit var CircularCountCalories: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weekly_steps, container, false)

        progressBarStepsMon = view.findViewById(R.id.progressbarStepsWeekMon)
        progressBarStepsTues = view.findViewById(R.id.progressbarStepsWeekTues)
        progressBarStepsWed = view.findViewById(R.id.progressbarStepsWeekWed)
        progressBarStepsThur = view.findViewById(R.id.progressbarStepsWeekThurs)
        progressBarStepsFri = view.findViewById(R.id.progressbarStepsWeekFri)
        progressBarStepsSatur = view.findViewById(R.id.progressbarStepsWeekSatur)
        progressBarStepsSun = view.findViewById(R.id.progressbarStepsWeekSun)

        countStepsMon = view.findViewById(R.id.stepsMon)
        countStepsTues = view.findViewById(R.id.stepsTues)
        countStepsWed = view.findViewById(R.id.stepsWed)
        countStepsThur = view.findViewById(R.id.stepsThur)

        countStepsFri = view.findViewById(R.id.stepsFri)
        countStepsSatur = view.findViewById(R.id.stepsSat)
        countStepsSun = view.findViewById(R.id.stepsSun)

        (activity as MainActivity).recordsViewModel.todaySteps.observe(
            viewLifecycleOwner,
            Observer { steps ->



                    steps.forEach { todaySteps ->
                        val countS = todaySteps.count.toString()

                        /// Todo: uso utente 1 da generalizzare in caso
                        val currentGoal =
                            (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

                        // Determina il giorno della settimana
                        val dayOfWeek = Helpers.getDayOfWeek(todaySteps.date)

                        if (currentGoal != null) {

                            when (dayOfWeek) {
                                "Monday" -> {
                                    countStepsMon.text = countS
                                    progressBarStepsMon.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }

                                "Tuesday" -> {
                                    countStepsTues.text = countS
                                    progressBarStepsTues.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }

                                "Wednesday" -> {
                                    countStepsWed.text = countS
                                    progressBarStepsWed.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }

                                "Thursday" -> {
                                    countStepsThur.text = countS
                                    progressBarStepsThur.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }

                                "Friday" -> {
                                    countStepsFri.text = countS
                                    progressBarStepsFri.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }

                                "Saturday" -> {
                                    countStepsSatur.text = countS
                                    progressBarStepsSatur.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }

                                "Sunday" -> {
                                    countStepsSun.text = countS
                                    progressBarStepsSun.progress = Helpers.calculatePercentage(
                                        countS.toInt(),
                                        currentGoal.steps.toDouble()
                                    )
                                }
                            }
                        }
                    }

            })
        return view
    }
}
/*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weekly_steps, container, false)

        progressBarStepsMon = view.findViewById(R.id.progressbarStepsWeekMon)
        progressBarStepsTues = view.findViewById(R.id.progressbarStepsWeekTues)
        progressBarStepsWed = view.findViewById(R.id.progressbarStepsWeekWed)
        progressBarStepsThur = view.findViewById(R.id.progressbarStepsWeekThurs)
        progressBarStepsFri = view.findViewById(R.id.progressbarStepsWeekFri)
        progressBarStepsSatur = view.findViewById(R.id.progressbarStepsWeekSatur)
        progressBarStepsSun = view.findViewById(R.id.progressbarStepsWeekSun)

        countStepsMon = view.findViewById(R.id.stepsMon)
        countStepsTues = view.findViewById(R.id.stepsTues)
        countStepsWed = view.findViewById(R.id.stepsWed)
        countStepsThur = view.findViewById(R.id.stepsThur)
        countStepsFri = view.findViewById(R.id.stepsFri)
        countStepsSatur = view.findViewById(R.id.stepsSat)
        countStepsSun = view.findViewById(R.id.stepsSun)

        (activity as MainActivity).recordsViewModel.weeklySteps.observe(viewLifecycleOwner, Observer{ records ->

            records?.let {

                records.forEach { record ->
                    val countS = record.steps.count.toString()
                    val countD = record.distance.count.toString()
                    val countC = record.calories.count.toString()

                    /// Todo: uso utente 1 da generalizzare in caso
                    val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

                    // Determina il giorno della settimana
                    val dayOfWeek =  Helpers.getDayOfWeek(record.steps.date)

                    if (currentGoal != null) {

                        when (dayOfWeek) {
                            "Monday" -> {
                                countStepsMon.text = countS
                                progressBarStepsMon.progress = Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                            "Tuesday" -> {
                                countStepsTues.text = countS
                                progressBarStepsTues.progress =  Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                            "Wednesday" -> {
                                countStepsWed.text = countS
                                progressBarStepsWed.progress =  Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                            "Thursday" -> {
                                countStepsThur.text = countS
                                progressBarStepsThur.progress =  Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                            "Friday" -> {
                                countStepsFri.text = countS
                                progressBarStepsFri.progress =  Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                            "Saturday" -> {
                                countStepsSatur.text = countS
                                progressBarStepsSatur.progress =  Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                            "Sunday" -> {
                                countStepsSun.text = countS
                                progressBarStepsSun.progress =  Helpers.calculatePercentage(countS.toInt(), currentGoal.steps.toDouble())
                            }
                        }
                    }
                }
            }
        })

        CircularProgressBarSteps = view.findViewById(R.id.ProgressbarSteps2)
        CircularProgressBarCalories = view.findViewById(R.id.ProgressbarCalories2)
        CircularProgressBarDistance = view.findViewById(R.id.ProgressbarDistance2)

        // todo ho un dubbi su dove devo trovarle
        CircularCountSteps = view.findViewById(R.id.countSteps2)
        CircularCountCalories = view.findViewById(R.id.countCalories2)
        CircularCountDistance = view.findViewById(R.id.countDistance2)

        // Imposta i click listener per i giorni della settimana
        progressBarStepsMon.setOnClickListener {
            handleProgressBarClick("Monday")
        }

        progressBarStepsTues.setOnClickListener {
            handleProgressBarClick("Tuesday")
        }

        progressBarStepsWed.setOnClickListener {
            handleProgressBarClick("Wednesday")
        }
        progressBarStepsThur.setOnClickListener {
            handleProgressBarClick("Thursday")
        }
        progressBarStepsFri.setOnClickListener {
            handleProgressBarClick("Friday")
        }
        progressBarStepsSatur.setOnClickListener {
            handleProgressBarClick("Saturday")
        }
        progressBarStepsSun.setOnClickListener {
            handleProgressBarClick("Sunday")
        }

        return view
    }

    private fun handleProgressBarClick(dayOfWeek: String) {

        // Ottieni le attività settimanali per il giorno specificato
        val dayActivities = (activity as MainActivity).recordsViewModel.weeklyUserActivities.value?.find { weeklyActivity ->
            Helpers.getDayOfWeek(weeklyActivity.steps.date) == dayOfWeek
        }

        // Se sono presenti attività per il giorno specificato
        if (dayActivities != null) {
            CircularCountSteps.text = dayActivities.steps.count.toString()
            CircularCountCalories.text = dayActivities.calories.count.toString()
            CircularCountDistance.text = dayActivities.distance.count.toString()

            // Ottieni l'obiettivo utente
            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
            if (currentGoal != null) {
                // Aggiorna le progress bar
                CircularProgressBarSteps.progress = Helpers.calculatePercentage(dayActivities.steps.count, currentGoal.steps.toDouble())
                CircularProgressBarCalories.progress = Helpers.calculatePercentage(dayActivities.calories.count, currentGoal.calories.toDouble())
                CircularProgressBarDistance.progress = Helpers.calculatePercentage(dayActivities.distance.count.toInt(), currentGoal.distance)
            }
        }
    }

}
*/