package it.unipd.footbyfoot.fragments.summary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.workout.Distance
import it.unipd.footbyfoot.fragments.settings.SettingsFragment
import java.time.LocalDate

class Last7SummariesFragment : Fragment() {


    private lateinit var StepsProgressBar1: ProgressBar
    private lateinit var StepsProgressBar2: ProgressBar
    private lateinit var StepsProgressBar3: ProgressBar
    private lateinit var StepsProgressBar4: ProgressBar
    private lateinit var StepsProgressBar5: ProgressBar
    private lateinit var StepsProgressBar6: ProgressBar
    private lateinit var StepsProgressBar7: ProgressBar

    private lateinit var countSteps1: TextView
    private lateinit var countSteps2: TextView
    private lateinit var countSteps3: TextView
    private lateinit var countSteps4: TextView
    private lateinit var countSteps5: TextView
    private lateinit var countSteps6: TextView
    private lateinit var countSteps7: TextView

    private lateinit var dateView: TextView


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
        val view = inflater.inflate(R.layout.fragment_last_7_summaries, container, false)

        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val weight = preferences.getInt(SettingsFragment.WEIGHT, SettingsFragment.defaultWeight)
        val height = preferences.getInt(SettingsFragment.HEIGHT, SettingsFragment.defaultHeight)

        val listProgressBar: List<ProgressBar> = listOf(
            view.findViewById(R.id.progressbarSteps1),
            view.findViewById(R.id.progressbarSteps2),
            view.findViewById(R.id.progressbarSteps3),
            view.findViewById(R.id.progressbarSteps4),
            view.findViewById(R.id.progressbarSteps5),
            view.findViewById(R.id.progressbarSteps6),
            view.findViewById(R.id.progressbarSteps7)
        )

        val listSteps: List<TextView> = listOf(
            view.findViewById(R.id.steps1),
            view.findViewById(R.id.steps2),
            view.findViewById(R.id.steps3),
            view.findViewById(R.id.steps4),
            view.findViewById(R.id.steps5),
            view.findViewById(R.id.steps6),
            view.findViewById(R.id.steps7)
        )

        dateView = view.findViewById(R.id.Date)

        CircularProgressBarSteps = view.findViewById(R.id.progressbarLastSteps)
        CircularProgressBarCalories = view.findViewById(R.id.progressbarLastCalories)
        CircularProgressBarDistance = view.findViewById(R.id.progressbarLastDistance)

        CircularCountSteps = view.findViewById(R.id.countLastSteps)
        CircularCountDistance = view.findViewById(R.id.countLastDistance)
        CircularCountCalories = view.findViewById(R.id.countLastCalories)

        // todo c'e un bug per cui se giro lo schermo tutte le bar hanno lo stesso valore

        (activity as MainActivity).recordsViewModel.lastWeekDistances.observe(viewLifecycleOwner, Observer { distanceList ->

            if (distanceList.isNotEmpty()) {

                val goals = (activity as MainActivity).recordsViewModel.allGoals.value

                if(goals != null){

                    for (i in 1..7) {
                        val date = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value-i).toLong())
                        var distance = Distance(0, date.year, date.dayOfYear)
                        for (distanceL in distanceList) {
                            val dayOfWeek = LocalDate.ofYearDay(distanceL.year, distanceL.dayOfYear).dayOfWeek.value
                            if (dayOfWeek == i) {
                                distance = distanceL
                                break
                            }
                        }

                        //TODO: metti in helpers
                        var goalPosition = 0
                        for (j in goals.indices) {
                            if (goals[j].year > distance.year || (goals[j].year == distance.year && goals[j].dayOfYear >= distance.dayOfYear)) {
                                goalPosition = j
                            } else {
                                break
                            }
                        }

                        val countD = distance.meters
                        val countS = Helpers.calculateSteps(height, distance.meters)
                        val countC = Helpers.calculateCalories(weight, distance.meters)

                        listSteps[i-1].text = countS.toString()
                        listProgressBar[i-1].progress = Helpers.calculatePercentage(countS.toDouble(), goals[goalPosition].steps.toDouble())

                        listProgressBar[i-1].setOnClickListener {

                                        dateView.text = Helpers.formatDateToString(distance.year, distance.dayOfYear)

                                        CircularCountDistance.text = countD.toString()

                                        CircularCountSteps.text = countS.toString()

                                        CircularCountCalories.text = countC.toString()

                                        CircularProgressBarDistance.progress= Helpers.calculatePercentage(countD.toDouble(), goals[goalPosition].distance)
                                        CircularProgressBarCalories.progress = Helpers.calculatePercentage(countC, goals[goalPosition].calories.toDouble())
                                        CircularProgressBarSteps.progress = Helpers.calculatePercentage(countS.toDouble(), goals[goalPosition].steps.toDouble())

                        }

                    }

                }
            }
        })

        listProgressBar[LocalDate.now().dayOfWeek.value].performClick()

        return view
    }

}


/*
 if (savedInstanceState != null) {
            StepsProgressBar1.progress = savedInstanceState.getInt("1Bar")
            StepsProgressBar2.progress = savedInstanceState.getInt("2Bar")
            StepsProgressBar3.progress = savedInstanceState.getInt("3Bar")
            StepsProgressBar4.progress = savedInstanceState.getInt("4Bar")
            StepsProgressBar5.progress = savedInstanceState.getInt("5Bar")
            StepsProgressBar6.progress = savedInstanceState.getInt("6Bar")
            StepsProgressBar7.progress = savedInstanceState.getInt("7Bar")

            date.text = savedInstanceState.getString("date")

            val countS1 = savedInstanceState.getString("1Count")
            if (countS1 != null) countSteps1.text = countS1
            val countS2 = savedInstanceState.getString("2Count")
            if (countS2 != null) countSteps2.text = countS2
            val countS3 = savedInstanceState.getString("3Count")
            if (countS3 != null) countSteps3.text = countS3
            val countS4 = savedInstanceState.getString("4Count")
            if (countS4 != null) countSteps4.text = countS4
            val countS5 = savedInstanceState.getString("5Count")
            if (countS5 != null) countSteps5.text = countS5
            val countS6 = savedInstanceState.getString("6Count")
            if (countS6 != null) countSteps6.text = countS6
            val countS7 = savedInstanceState.getString("7Count")
            if (countS7 != null) countSteps7.text = countS7

            CircularProgressBarSteps.progress = savedInstanceState.getInt("stepsCircular")
            CircularProgressBarCalories.progress = savedInstanceState.getInt("caloriesCircular")
            CircularProgressBarDistance.progress = savedInstanceState.getInt("distanceCircular")

            val stepsCircularCount = savedInstanceState.getString("stepsCircularCount")
            if (stepsCircularCount!= null) CircularCountSteps.text = stepsCircularCount
            val caloriesCircularCount = savedInstanceState.getString("caloriesCircularCount")
            if (caloriesCircularCount!= null) CircularCountCalories.text = caloriesCircularCount
            val distanceCircularCount = savedInstanceState.getString("distanceCircularCount")
            if (distanceCircularCount!= null) CircularCountDistance.text = distanceCircularCount

        }

        override fun onSaveInstanceState(savedInstanceState: Bundle)
    {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("date", date.text.toString())

        savedInstanceState.putInt("1Bar", StepsProgressBar1.progress)
        savedInstanceState.putInt("2Bar", StepsProgressBar2.progress)
        savedInstanceState.putInt("3Bar", StepsProgressBar3.progress)
        savedInstanceState.putInt("4Bar", StepsProgressBar4.progress)
        savedInstanceState.putInt("5Bar", StepsProgressBar5.progress)
        savedInstanceState.putInt("6Bar", StepsProgressBar6.progress)
        savedInstanceState.putInt("7Bar", StepsProgressBar7.progress)

        savedInstanceState.putString("1Count", countSteps1.text.toString())
        savedInstanceState.putString("2Count", countSteps2.text.toString())
        savedInstanceState.putString("3Count", countSteps3.text.toString())
        savedInstanceState.putString("4Count", countSteps4.text.toString())
        savedInstanceState.putString("5Count", countSteps5.text.toString())
        savedInstanceState.putString("6Count", countSteps6.text.toString())
        savedInstanceState.putString("7Count", countSteps7.text.toString())

        savedInstanceState.putInt("stepsCircular", CircularProgressBarSteps.progress)
        savedInstanceState.putInt("caloriesCircular", CircularProgressBarCalories.progress)
        savedInstanceState.putInt("distanceCircular", CircularProgressBarDistance.progress)

        savedInstanceState.putString("stepsCircularCount", CircularCountSteps.text.toString())
        savedInstanceState.putString("caloriesCircularCount", CircularCountCalories.text.toString())
        savedInstanceState.putString("distanceCircularCount", CircularCountDistance.text.toString())

    }
 */