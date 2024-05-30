package it.unipd.footbyfoot.fragments.goals

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.goal.Goal
import java.time.LocalDate


class GoalsFragment : Fragment() {
    // Class constants
    companion object {
        private const val STEPS_GOAL = "stepsGoal"
        private const val CALORIES_GOAL = "caloriesGoal"
        private const val DISTANCE_GOAL = "distanceGoal"
    }

    // Class variables
    private lateinit var stepsGoal: TextView
    private lateinit var caloriesGoal: TextView
    private lateinit var distanceGoal: TextView

    private lateinit var addStepsButton: ImageButton
    private lateinit var subStepsButton: ImageButton
    private lateinit var addCaloriesButton: ImageButton
    private lateinit var subCaloriesButton: ImageButton
    private lateinit var addDistanceButton: ImageButton
    private lateinit var subDistanceButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        stepsGoal = view.findViewById(R.id.stepsGoalCount)
        caloriesGoal = view.findViewById(R.id.caloriesGoalCount)
        distanceGoal = view.findViewById(R.id.distanceGoalCount)

        addStepsButton = view.findViewById(R.id.addStepsButton)
        subStepsButton = view.findViewById(R.id.subStepsButton)
        addCaloriesButton = view.findViewById(R.id.addCaloriesButton)
        subCaloriesButton = view.findViewById(R.id.subCaloriesButton)
        addDistanceButton = view.findViewById(R.id.addDistanceButton)
        subDistanceButton = view.findViewById(R.id.subDistanceButton)

        // Carica i valori precedenti dai Preferences
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        stepsGoal.text = preferences.getInt(STEPS_GOAL, 0).toString()
        caloriesGoal.text = preferences.getInt(CALORIES_GOAL, 0).toString()
        distanceGoal.text = preferences.getInt(DISTANCE_GOAL, 0).toString()

        if (savedInstanceState != null) {
            stepsGoal.text = savedInstanceState.getInt(STEPS_GOAL, 0).toString()
            caloriesGoal.text = savedInstanceState.getInt(CALORIES_GOAL, 0).toString()
            distanceGoal.text = savedInstanceState.getInt(DISTANCE_GOAL, 0).toString()
        }

        addStepsButton.setOnClickListener {

            increment100Goal(stepsGoal)

            insertNewGoal()
        }

        subStepsButton.setOnClickListener {

            decrement100Goal(stepsGoal)

            insertNewGoal()
        }
        addCaloriesButton.setOnClickListener {

            increment100Goal(caloriesGoal)

            insertNewGoal()
        }
        subCaloriesButton.setOnClickListener {

            decrement100Goal(caloriesGoal)

            insertNewGoal()
        }
        addDistanceButton.setOnClickListener {

            incrementGoal(distanceGoal)

            insertNewGoal()
        }
        subDistanceButton.setOnClickListener {

            decrementGoal(distanceGoal)

            insertNewGoal()
        }

        return view
    }

    private fun insertNewGoal() {
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

    override fun onPause() {
        super.onPause()
        // Salva i valori nei Preferences
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(STEPS_GOAL, stepsGoal.text.toString().toInt())
        editor.putInt(CALORIES_GOAL, caloriesGoal.text.toString().toInt())
        editor.putInt(DISTANCE_GOAL, distanceGoal.text.toString().toInt())
        editor.apply()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle)
    {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(STEPS_GOAL, stepsGoal.text.toString().toInt())
        savedInstanceState.putInt(CALORIES_GOAL, caloriesGoal.text.toString().toInt())
        savedInstanceState.putInt(DISTANCE_GOAL, distanceGoal.text.toString().toInt())
    }

    private fun incrementGoal(textView: TextView) {
        var value = textView.text.toString().toInt()
        value++
        textView.text = value.toString()
    }

    private fun decrementGoal(textView: TextView) {
        var value = textView.text.toString().toInt()
        if (value > 0) {
            value--
            textView.text = value.toString()
        }
    }

    private fun increment100Goal(textView: TextView) {
        var value = textView.text.toString().toInt()
        value += 100
        textView.text = value.toString()
    }

    private fun decrement100Goal(textView: TextView) {
        var value = textView.text.toString().toInt()
        if (value > 0) {
            value -= 100
            textView.text = value.toString()
        }
    }
}