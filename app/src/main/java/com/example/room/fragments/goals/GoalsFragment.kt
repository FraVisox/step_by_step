package com.example.room.fragments.goals

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.room.MainActivity
import com.example.room.R
import com.example.room.database.RecordsViewModel


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

        addStepsButton.setOnClickListener {

            increment100Goal(stepsGoal)
            //todo: mettere una costante al posto dell'utente, noi usiamo solo un utente e quindi ok
            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {

                // prendo il valore da aggiornare
                val updatedSteps = stepsGoal.text.toString().toInt()
                // creo una copia dell'oggetto currentGoal,
                // ma con il valore del campo steps aggiornato al nuovo valore updatedSteps
                val updatedGoal = currentGoal.copy(steps = updatedSteps)
                // aggiorno l'oggetto poi
                (activity as MainActivity).recordsViewModel.updateGoal(updatedGoal)
            }
        }

        subStepsButton.setOnClickListener {

            decrement100Goal(stepsGoal)
            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {

                // prendo il valore da aggiornare
                val updatedSteps = stepsGoal.text.toString().toInt()
                // creo una copia dell'oggetto currentGoal,
                // ma con il valore del campo steps aggiornato al nuovo valore updatedSteps
                val updatedGoal = currentGoal.copy(steps = updatedSteps)
                // aggiorno l'oggetto poi
                (activity as MainActivity).recordsViewModel.updateGoal(updatedGoal)
            }
        }
        addCaloriesButton.setOnClickListener {

            increment100Goal(caloriesGoal)
            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {

                // prendo il valore da aggiornare
                val updatedCalories = caloriesGoal.text.toString().toInt()
                // creo una copia dell'oggetto currentGoal esistente,
                // ma con il valore del campo calories aggiornato al nuovo valore updatedSteps
                val updatedGoal = currentGoal.copy(calories = updatedCalories)
                // aggiorno l'oggetto poi
                (activity as MainActivity).recordsViewModel.updateGoal(updatedGoal)
            }
        }
        subCaloriesButton.setOnClickListener {

            decrement100Goal(caloriesGoal)

            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {

                // prendo il valore da aggiornare
                val updatedCalories = caloriesGoal.text.toString().toInt()
                // creo una copia dell'oggetto currentGoal esistente,
                // ma con il valore del campo calories aggiornato al nuovo valore updatedSteps
                val updatedGoal = currentGoal.copy(calories = updatedCalories)
                // aggiorno l'oggetto poi
                (activity as MainActivity).recordsViewModel.updateGoal(updatedGoal)
            }
        }
        addDistanceButton.setOnClickListener {

            incrementGoal(distanceGoal)

            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {

                // prendo il valore da aggiornare
                val updatedDistance = distanceGoal.text.toString().toDouble()
                // creo una copia dell'oggetto currentGoal esistente,
                // ma con il valore del campo distance aggiornato al nuovo valore updatedSteps
                val updatedGoal = currentGoal.copy(distance = updatedDistance)
                // aggiorno l'oggetto poi
                (activity as MainActivity).recordsViewModel.updateGoal(updatedGoal)
            }
        }
        subDistanceButton.setOnClickListener {

            decrementGoal(distanceGoal)

            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {

                // prendo il valore da aggiornare
                val updatedDistance = distanceGoal.text.toString().toDouble()
                // creo una copia dell'oggetto currentGoal esistente,
                // ma con il valore del campo distance aggiornato al nuovo valore updatedSteps
                val updatedGoal = currentGoal.copy(distance = updatedDistance)
                // aggiorno l'oggetto poi
                (activity as MainActivity).recordsViewModel.updateGoal(updatedGoal)
            }
        }

        return view
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