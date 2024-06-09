package it.unipd.footbyfoot.fragments.goals

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

class GoalsFragment : Fragment() {

    //Companion object
    companion object {
        const val stepsKey = "steps"
        const val distanceKey = "distance"
        const val caloriesKey = "calories"
        const val defaultGoal = 0

        //Firebase keys for parameters
        const val incrementCalories = "calories_increment"
        const val decrementCalories = "calories_decrement"
        const val incrementSteps = "steps_increment"
        const val decrementSteps = "steps_decrement"
        const val incrementDistance = "distance_increment"
        const val decrementDistance = "distance_decrement"
    }

    // Text views with the values of the goals
    private var stepsGoal: TextView? = null
    private var caloriesGoal: TextView? = null
    private var distanceGoal: TextView? = null

    //Counters used for the events in firebase
    private var counterIncrementCalories = 0
    private var counterIncrementSteps = 0
    private var counterIncrementDistance = 0
    private var counterDecrementCalories = 0
    private var counterDecrementSteps = 0
    private var counterDecrementDistance = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        //Initialize views
        stepsGoal = view.findViewById(R.id.stepsGoalCount)
        caloriesGoal = view.findViewById(R.id.caloriesGoalCount)
        distanceGoal = view.findViewById(R.id.distanceGoalCount)
        val addStepsButton: AppCompatImageButton = view.findViewById(R.id.addStepsButton)
        val subStepsButton: AppCompatImageButton = view.findViewById(R.id.subStepsButton)
        val addCaloriesButton: AppCompatImageButton = view.findViewById(R.id.addCaloriesButton)
        val subCaloriesButton: AppCompatImageButton = view.findViewById(R.id.subCaloriesButton)
        val addDistanceButton: AppCompatImageButton = view.findViewById(R.id.addDistanceButton)
        val subDistanceButton: AppCompatImageButton = view.findViewById(R.id.subDistanceButton)

        //Get saved goals. We take these values and not the ones in the database
        //as sometimes may happen that the inserting in the database and updating of livedata is
        //not instantaneous
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        stepsGoal?.text = preferences.getInt(stepsKey, defaultGoal).toString()
        caloriesGoal?.text = preferences.getInt(caloriesKey, defaultGoal).toString()
        distanceGoal?.text = preferences.getInt(distanceKey, defaultGoal).toString()

        //Initialize the listeners of the buttons
        addStepsButton.setOnClickListener {
            Helpers.increment100Value(stepsGoal)
            counterIncrementSteps++
        }
        subStepsButton.setOnClickListener {
            Helpers.decrement100Value(stepsGoal)
            counterDecrementSteps++
        }
        addCaloriesButton.setOnClickListener {
            Helpers.increment100Value(caloriesGoal)
            counterIncrementCalories++
        }
        subCaloriesButton.setOnClickListener {
            Helpers.decrement100Value(caloriesGoal)
            counterDecrementCalories++
        }
        addDistanceButton.setOnClickListener {
            Helpers.increment100Value(distanceGoal)
            counterIncrementDistance++
        }
        subDistanceButton.setOnClickListener {
            Helpers.decrement100Value(distanceGoal)
            counterDecrementDistance++
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        counterDecrementCalories = 0
        counterIncrementCalories = 0
        counterDecrementDistance = 0
        counterIncrementDistance = 0
        counterIncrementSteps = 0
        counterDecrementSteps = 0
    }

    override fun onPause() {
        super.onPause()
        //Insert the current goal into the database and the preferences
        insertGoal()

        //User properties
        (activity as MainActivity).firebaseAnalytics.setUserProperty(RecordsApplication.caloriesGoal, caloriesGoal?.text.toString())
        (activity as MainActivity).firebaseAnalytics.setUserProperty(RecordsApplication.stepsGoal, stepsGoal?.text.toString())
        (activity as MainActivity).firebaseAnalytics.setUserProperty(RecordsApplication.distanceGoal, distanceGoal?.text.toString())

        if (counterIncrementCalories != 0 || counterDecrementCalories != 0 || counterIncrementSteps != 0 ||
            counterDecrementSteps != 0 || counterIncrementDistance != 0 || counterDecrementDistance != 0) {
            val bundle = Bundle()
            bundle.putInt(incrementCalories, counterIncrementCalories)
            bundle.putInt(decrementCalories, counterDecrementCalories)
            bundle.putInt(incrementSteps, counterIncrementSteps)
            bundle.putInt(decrementSteps, counterDecrementSteps)
            bundle.putInt(incrementDistance, counterIncrementDistance)
            bundle.putInt(decrementDistance, counterDecrementDistance)
            (activity as MainActivity).firebaseAnalytics.logEvent(RecordsApplication.goalsUpdate, bundle)
        }
    }

    private fun insertGoal() {
        val updatedSteps = stepsGoal?.text.toString().toInt()
        val updatedDistance = distanceGoal?.text.toString().toInt()
        val updatedCalories = caloriesGoal?.text.toString().toInt()

        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        //Get last values (to know if we need to update or not)
        val currentDistance = preferences.getInt(distanceKey, defaultGoal)
        val currentSteps = preferences.getInt(stepsKey, defaultGoal)
        val currentCalories = preferences.getInt(caloriesKey, defaultGoal)

        //Insert everything in shared preferences
        val editor = preferences.edit()
        editor.putInt(caloriesKey, updatedCalories)
        editor.putInt(stepsKey, updatedSteps)
        editor.putInt(distanceKey, updatedDistance)
        editor.apply()

        //Insert a new goal only if it is different from the current one (to diminish entries of database)
        if (currentDistance != updatedDistance || currentSteps != updatedSteps || currentCalories != updatedCalories) {
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

}