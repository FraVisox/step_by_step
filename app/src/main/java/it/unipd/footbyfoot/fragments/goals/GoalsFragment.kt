package it.unipd.footbyfoot.fragments.goals

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
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
    }

    // Text views with the values of the goals
    private var stepsGoal: TextView? = null
    private var caloriesGoal: TextView? = null
    private var distanceGoal: TextView? = null

    //Done for efficiency
    private lateinit var currentGoal: Goal

    //Personalized trace
    private val goalTrace = Firebase.performance.newTrace("Goal_trace")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        //Start trace
        goalTrace.putMetric("Increment goals", 0)
        goalTrace.putMetric("Decrement goals", 0)
        goalTrace.start()

        //Initialize views
        stepsGoal = view.findViewById(R.id.stepsGoalCount)
        caloriesGoal = view.findViewById(R.id.caloriesGoalCount)
        distanceGoal = view.findViewById(R.id.distanceGoalCount)

        //Get saved goals. We take these values and not the ones in the database
        //as sometimes may happen that the inserting in the database and update of livedata is
        //not instantaneous
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        stepsGoal?.text = preferences.getInt(stepsKey, defaultGoal).toString()
        caloriesGoal?.text = preferences.getInt(caloriesKey, defaultGoal).toString()
        distanceGoal?.text = preferences.getInt(distanceKey, defaultGoal).toString()

        val addStepsButton: AppCompatImageButton = view.findViewById(R.id.addStepsButton)
        val subStepsButton: AppCompatImageButton = view.findViewById(R.id.subStepsButton)
        val addCaloriesButton: AppCompatImageButton = view.findViewById(R.id.addCaloriesButton)
        val subCaloriesButton: AppCompatImageButton = view.findViewById(R.id.subCaloriesButton)
        val addDistanceButton: AppCompatImageButton = view.findViewById(R.id.addDistanceButton)
        val subDistanceButton: AppCompatImageButton = view.findViewById(R.id.subDistanceButton)

        addStepsButton.setOnClickListener {
            Helpers.increment100Value(stepsGoal!!)
            goalTrace.incrementMetric(getString(R.string.increment_goals), 1)
        }
        subStepsButton.setOnClickListener {
            Helpers.decrement100Value(stepsGoal!!)
            goalTrace.incrementMetric(getString(R.string.decrement_goals), 1)
        }
        addCaloriesButton.setOnClickListener {
            Helpers.increment100Value(caloriesGoal!!)
            goalTrace.incrementMetric(getString(R.string.increment_goals), 1)
        }
        subCaloriesButton.setOnClickListener {
            Helpers.decrement100Value(caloriesGoal!!)
            goalTrace.incrementMetric(getString(R.string.decrement_goals), 1)
        }
        addDistanceButton.setOnClickListener {
            Helpers.increment100Value(distanceGoal!!)
            goalTrace.incrementMetric(getString(R.string.increment_goals), 1)
        }
        subDistanceButton.setOnClickListener {
            Helpers.decrement100Value(distanceGoal!!)
            goalTrace.incrementMetric(getString(R.string.decrement_goals), 1)
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (caloriesGoal != null && distanceGoal != null && stepsGoal != null) {
            outState.putInt(caloriesKey, caloriesGoal!!.text.toString().toInt())
            outState.putInt(distanceKey, distanceGoal!!.text.toString().toInt())
            outState.putInt(stepsKey, stepsGoal!!.text.toString().toInt())
        }
    }

    override fun onPause() {
        super.onPause()

        //Insert the current goal into the database
        insertGoal()
        //End trace
        goalTrace.stop()
    }

    private fun insertGoal() {
        val updatedSteps = stepsGoal?.text.toString().toInt()
        val updatedDistance = distanceGoal?.text.toString().toInt()
        val updatedCalories = caloriesGoal?.text.toString().toInt()

        //Insert everything in shared preferences
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(caloriesKey, updatedCalories)
        editor.putInt(stepsKey, updatedSteps)
        editor.putInt(distanceKey, updatedDistance)
        editor.apply()

        //Insert a new goal only if it is different from the current one (for efficiency)
        if (currentGoal.distance != updatedDistance || currentGoal.steps != updatedSteps || currentGoal.calories != updatedCalories) {
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