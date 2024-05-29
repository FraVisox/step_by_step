package it.unipd.footbyfoot.fragments.steps

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
import it.unipd.footbyfoot.database.goal.Goal

class TodayStepsFragment : Fragment() {

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        progressBarSteps = view.findViewById(R.id.progressbarStepsToday)
        countSteps = view.findViewById(R.id.countStepsToday)
        goalsSteps = view.findViewById(R.id.goalsStepsToday)
        progressBarCalories = view.findViewById(R.id.progressbarCaloriesToday)
        countCalories = view.findViewById(R.id.countCaloriesToday)
        goalsCalories= view.findViewById(R.id.goalsCaloriesToday)
        progressBarDistance = view.findViewById(R.id.progressbarDistanceToday)
        countDistance = view.findViewById(R.id.countDistanceToday)
        goalsDistance = view.findViewById(R.id.goalsDistanceToday)


        Log.d("RecordsRoomDatabase", "todaystepsfragment")




        // todo bug enorme se lo tolgo non va niente !!
        (activity as MainActivity).recordsViewModel.userGoal.observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                val goalsString = goals.joinToString(separator = "\n") { goals ->
                    " UserID: ${goals.userId}, Steps: ${goals.steps}, Date: ${goals.distance}"
                }
                // Logga il contenuto della lista
                Log.d("oggi", goalsString)
            } else {
                Log.d("oggi", "La lista dei passi di oggi è vuota")
            }
        })
        (activity as MainActivity).recordsViewModel.allUsers.observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                val goalsString = goals.joinToString(separator = "\n") { goals ->
                    "OK"
                }
                // Logga il contenuto della lista
                Log.d("oggi", goalsString)
            } else {
                Log.d("oggi", "La lista dei Users di oggi è vuota")
            }
        })
        (activity as MainActivity).recordsViewModel.lastDistance.observe(viewLifecycleOwner, Observer { distance ->

            if (distance.isNotEmpty() && distance.size == 1) {
                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                val currentUser = (activity as MainActivity).recordsViewModel.allUsers.value?.find { it.userId == 1 }


                if(currentUser != null && currentGoal != null){

                    val countD = distance.last().count.toString()
                    countDistance.text = countD

                    val countS = Helpers.calculateSteps(currentUser.height.toDouble(), distance.last().count )
                    countSteps.text = countS.toString()

                    val countC = Helpers.calculateCalories(currentUser.weight.toDouble(), distance.last().count)
                    countCalories.text = countC.toString()

                    goalsCalories.text = currentGoal.calories.toString()
                    goalsSteps.text = currentGoal.steps.toString()
                    goalsDistance.text = currentGoal.distance.toString()

                    progressBarDistance.progress = Helpers.calculatePercentage(distance.last().count, currentGoal.distance)
                    progressBarCalories.progress = Helpers.calculatePercentage(countC, currentGoal.calories.toDouble())
                    progressBarSteps.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())

                }
            }

        })
        
        return view
    }


}
