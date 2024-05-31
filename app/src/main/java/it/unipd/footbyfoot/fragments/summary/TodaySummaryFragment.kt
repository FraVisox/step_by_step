package it.unipd.footbyfoot.fragments.summary

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.settings.SettingsFragment

class TodaySummaryFragment : Fragment() {

    private lateinit var progressBarSteps  : ProgressBar
    private lateinit var countSteps: TextView
    private lateinit var goalsSteps : TextView

    private lateinit var progressBarCalories : ProgressBar
    private lateinit var countCalories : TextView
    private lateinit var goalsCalories : TextView

    private lateinit var progressBarDistance : ProgressBar
    private lateinit var countDistance : TextView
    private lateinit var goalsDistance : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_today_summary, container, false)

        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val weight = preferences.getInt(SettingsFragment.WEIGHT, 0)
        val height = preferences.getInt(SettingsFragment.HEIGHT, 0)

        progressBarSteps = view.findViewById(R.id.progressbarTodaySteps)
        countSteps = view.findViewById(R.id.countTodaySteps)
        goalsSteps = view.findViewById(R.id.goalsTodaySteps)
        progressBarCalories = view.findViewById(R.id.progressbarTodayCalories)
        countCalories = view.findViewById(R.id.countTodayCalories)
        goalsCalories= view.findViewById(R.id.goalsTodayCalories)
        progressBarDistance = view.findViewById(R.id.progressbarLastDistance)
        countDistance = view.findViewById(R.id.countLastDistance)
        goalsDistance = view.findViewById(R.id.goalsLastDistance)



        // todo bug enorme se lo tolgo non va niente !!

        (activity as MainActivity).recordsViewModel.allGoals.observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                // Logga il contenuto della lista
            } else {
                Log.d("oggi", "La lista dei passi di oggi è vuota")
            }
        })

        (activity as MainActivity).recordsViewModel.todayDistance.observe(viewLifecycleOwner, Observer { distance ->

            if (distance.size == 1) {
                val currentGoal = (activity as MainActivity).recordsViewModel.lastGoal.value

                if( currentGoal != null){

                    val countD = distance.last().meters.toString()
                    countDistance.text = countD

                    val countS = Helpers.calculateSteps(height, distance.last().meters )
                    countSteps.text = countS.toString()

                    val countC = Helpers.calculateCalories(weight, distance.last().meters)
                    countCalories.text = countC.toString()

                    goalsCalories.text = currentGoal.calories.toString()
                    goalsSteps.text = currentGoal.steps.toString()
                    goalsDistance.text = currentGoal.distance.toString()

                    progressBarDistance.progress = Helpers.calculatePercentage(Helpers.distanceToKm(distance.last().meters), currentGoal.distance)
                    progressBarCalories.progress = Helpers.calculatePercentage(countC, currentGoal.calories.toDouble())
                    progressBarSteps.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())

                }
            }

        })
        
        return view
    }


}
