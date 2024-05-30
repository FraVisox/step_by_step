package it.unipd.footbyfoot.fragments.summary

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

    private lateinit var date: TextView


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

        StepsProgressBar1 = view.findViewById(R.id.progressbarSteps1)
        StepsProgressBar2 = view.findViewById(R.id.progressbarSteps2)
        StepsProgressBar3 = view.findViewById(R.id.progressbarSteps3)
        StepsProgressBar4 = view.findViewById(R.id.progressbarSteps4)
        StepsProgressBar5 = view.findViewById(R.id.progressbarSteps5)
        StepsProgressBar6 = view.findViewById(R.id.progressbarSteps6)
        StepsProgressBar7 = view.findViewById(R.id.progressbarSteps7)


        countSteps1 = view.findViewById(R.id.steps1)
        countSteps2 = view.findViewById(R.id.steps2)
        countSteps3 = view.findViewById(R.id.steps3)
        countSteps4 = view.findViewById(R.id.steps4)
        countSteps5 = view.findViewById(R.id.steps5)
        countSteps6 = view.findViewById(R.id.steps6)
        countSteps7 = view.findViewById(R.id.steps7)

        date = view.findViewById(R.id.Date)

        CircularProgressBarSteps = view.findViewById(R.id.progressbarLastSteps)
        CircularProgressBarCalories = view.findViewById(R.id.progressbarLastCalories)
        CircularProgressBarDistance = view.findViewById(R.id.progressbarLastDistance)

        CircularCountSteps = view.findViewById(R.id.countLastSteps)
        CircularCountDistance = view.findViewById(R.id.countLastDistance)
        CircularCountCalories = view.findViewById(R.id.countLastCalories)

        // todo c'e un bug per cui se giro lo schermo tutte le bar hanno lo stesso valore

        (activity as MainActivity).recordsViewModel.last7Distances.observe(viewLifecycleOwner, Observer { distanceList ->

            if (distanceList.isNotEmpty()) {

                handleProgressBarClick(1)

                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                val currentUser = (activity as MainActivity).recordsViewModel.allUsers.value?.find { it.userId == 1 }

                if(currentUser != null && currentGoal != null){

                    val size = distanceList.size

                    var countS = Helpers.calculateSteps( currentUser.height.toDouble(), distanceList.last().count)
                    countSteps1.text = countS.toString()
                    StepsProgressBar1.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())


                    if(size > 1){
                        countS = Helpers.calculateSteps( currentUser.height.toDouble(), distanceList[size-2].count)
                        StepsProgressBar2.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
                        countSteps2.text = countS.toString()
                    }
                    if(size > 2){
                        countS = Helpers.calculateSteps( currentUser.height.toDouble(), distanceList[size-3].count)
                        StepsProgressBar3.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
                        countSteps3.text = countS.toString()
                    }
                    if(size > 3){
                        countS = Helpers.calculateSteps( currentUser.height.toDouble(), distanceList[size-4].count)
                        StepsProgressBar4.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
                        countSteps4.text = countS.toString()
                    }
                    if(size > 4){
                        countS = Helpers.calculateSteps( currentUser.height.toDouble(), distanceList[size-5].count,)
                        StepsProgressBar5.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
                        countSteps5.text = countS.toString()
                    }
                    if(size > 5){
                        countS = Helpers.calculateSteps( currentUser.height.toDouble(), distanceList[size-6].count)
                        StepsProgressBar6.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
                        countSteps6.text = countS.toString()
                    }
                    if(size > 6){
                        countS = Helpers.calculateSteps(currentUser.height.toDouble(), distanceList[size-7].count)
                        StepsProgressBar7.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())
                        countSteps7.text = countS.toString()
                    }

                }
            }
        })

        StepsProgressBar1.setOnClickListener{
            handleProgressBarClick(1)
        }
        StepsProgressBar2.setOnClickListener{
            handleProgressBarClick(2)
        }
        StepsProgressBar3.setOnClickListener {
            handleProgressBarClick(3)
        }
        StepsProgressBar4.setOnClickListener {
            handleProgressBarClick(4)
        }
        StepsProgressBar5.setOnClickListener {
            handleProgressBarClick(5)
        }
        StepsProgressBar6.setOnClickListener {
            handleProgressBarClick(6)
        }
        StepsProgressBar7.setOnClickListener {
            handleProgressBarClick(7)
        }
        return view
    }

    private fun handleProgressBarClick(num : Int) {

        (activity as MainActivity).recordsViewModel.last7Distances.observe( viewLifecycleOwner, Observer { distanceList ->

            val size = distanceList.size
            if(size >= num && distanceList.isNotEmpty()) {

                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                val currentUser = (activity as MainActivity).recordsViewModel.allUsers.value?.find { it.userId == 1 }


                if(currentUser != null && currentGoal != null){

                    date.text = Helpers.formatDateToString(distanceList[size-num].date)

                    val countD = distanceList[size-num].count.toString()
                    CircularCountDistance.text = countD

                    val countS = Helpers.calculateSteps(currentUser.height.toDouble(), distanceList[size-num].count)
                    CircularCountSteps.text = countS.toString()

                    val countC = Helpers.calculateCalories(currentUser.weight.toDouble(),distanceList[size-num].count)
                    CircularCountCalories.text = countC.toString()

                    CircularProgressBarDistance.progress= Helpers.calculatePercentage(countD.toDouble(), currentGoal.distance)
                    CircularProgressBarCalories.progress = Helpers.calculatePercentage(countC, currentGoal.calories.toDouble())
                    CircularProgressBarSteps.progress = Helpers.calculatePercentage(countS.toDouble(), currentGoal.steps.toDouble())

                }

            }
        })

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