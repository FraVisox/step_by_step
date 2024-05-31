package it.unipd.footbyfoot.fragments.goals

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.fragments.summary.Helpers
import java.time.LocalDate


class GoalsFragment : Fragment() {

    // Text views with the values of the goals
    private lateinit var stepsGoal: TextView
    private lateinit var caloriesGoal: TextView
    private lateinit var distanceGoal: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        Log.d("AAA", "goals fragment created")

        //Initialize views
        stepsGoal = view.findViewById(R.id.stepsGoalCount)
        caloriesGoal = view.findViewById(R.id.caloriesGoalCount)
        distanceGoal = view.findViewById(R.id.distanceGoalCount)

        //Take the last goal, if present, or the default goal
        val currentGoal = (activity as MainActivity).recordsViewModel.allGoals.value?.first() ?: Helpers.defaultGoal

        stepsGoal.text = currentGoal.steps.toString()
        caloriesGoal.text = currentGoal.calories.toString()
        distanceGoal.text = currentGoal.distance.toString()

        val addStepsButton: AppCompatImageButton = view.findViewById(R.id.addStepsButton)
        val subStepsButton: AppCompatImageButton = view.findViewById(R.id.subStepsButton)
        val addCaloriesButton: AppCompatImageButton = view.findViewById(R.id.addCaloriesButton)
        val subCaloriesButton: AppCompatImageButton = view.findViewById(R.id.subCaloriesButton)
        val addDistanceButton: AppCompatImageButton = view.findViewById(R.id.addDistanceButton)
        val subDistanceButton: AppCompatImageButton = view.findViewById(R.id.subDistanceButton)

        addStepsButton.setOnClickListener {
            Helpers.increment100Value(stepsGoal)
        }
        subStepsButton.setOnClickListener {
            Helpers.decrement100Value(stepsGoal)
        }
        addCaloriesButton.setOnClickListener {
            Helpers.increment100Value(caloriesGoal)
        }
        subCaloriesButton.setOnClickListener {
            Helpers.decrement100Value(caloriesGoal)
        }
        addDistanceButton.setOnClickListener {
            Helpers.incrementValue(distanceGoal)
        }
        subDistanceButton.setOnClickListener {
            Helpers.decrementValue(distanceGoal)
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        insertGoal()
    }

    private fun insertGoal() {
        val updatedSteps = stepsGoal.text.toString().toInt()
        val updatedDistance = distanceGoal.text.toString().toDouble()
        val updatedCalories = caloriesGoal.text.toString().toInt()

        val date = LocalDate.now()
        val updatedGoal = Goal(
            date.year,
            date.dayOfYear,
            updatedSteps,
            updatedCalories,
            updatedDistance
        )
        (activity as MainActivity).recordsViewModel.insertGoal(updatedGoal)
    }

}