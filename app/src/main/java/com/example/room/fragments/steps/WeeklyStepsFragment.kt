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

class WeeklyStepsFragment : Fragment() {


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
        val view = inflater.inflate(R.layout.fragment_weekly_steps, container, false)

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

        CircularProgressBarSteps = view.findViewById(R.id.progressbarStepsToday)
        CircularProgressBarCalories = view.findViewById(R.id.progressbarCaloriesToday)
        CircularProgressBarDistance = view.findViewById(R.id.progressbarDistanceToday)

        CircularCountSteps = view.findViewById(R.id.countStepsToday)
        CircularCountDistance = view.findViewById(R.id.countDistanceToday)
        CircularCountCalories = view.findViewById(R.id.countCaloriesToday)

        // todo c'e un bug per cui se giro lo schermo tutte le bar hanno lo stesso valore
        if (savedInstanceState != null) {
            val progress1 = savedInstanceState.getInt("1Bar")
            if (progress1 != 0) StepsProgressBar1.progress = progress1
            val progress2 = savedInstanceState.getInt("2Bar")
            if (progress2 != 0) StepsProgressBar2.progress = progress2
            val progress3 = savedInstanceState.getInt("3Bar")
            if (progress3 != 0) StepsProgressBar3.progress = progress3
            val progress4 = savedInstanceState.getInt("4Bar")
            if (progress4 != 0) StepsProgressBar4.progress = progress4
            val progress5 = savedInstanceState.getInt("5Bar")
            if (progress5 != 0) StepsProgressBar5.progress = progress5
            val progress6 = savedInstanceState.getInt("6Bar")
            if (progress6 != 0) StepsProgressBar6.progress = progress6
            val progress7 = savedInstanceState.getInt("7Bar")
            if (progress7 != 0) StepsProgressBar7.progress = progress7

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


        (activity as MainActivity).recordsViewModel.weeklySteps.observe(viewLifecycleOwner, Observer { stepsList ->

            if (stepsList.isNotEmpty()) {

                date.text = Helpers.formatDateToString(stepsList.last().date)
                handleProgressBarClick(1)

                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

                if (currentGoal != null) {

                    val size = stepsList.size
                    StepsProgressBar1.progress = Helpers.calculatePercentage(stepsList.last().count, currentGoal.steps.toDouble())
                    countSteps1.text = stepsList.last().count.toString()

                    if(size > 1){
                        StepsProgressBar2.progress = Helpers.calculatePercentage(stepsList[size-2].count, currentGoal.steps.toDouble())
                        countSteps2.text = stepsList[size-2].count.toString()
                    }
                    if(size > 2){
                        StepsProgressBar3.progress = Helpers.calculatePercentage(stepsList[size-3].count, currentGoal.steps.toDouble())
                        countSteps3.text = stepsList[size-3].count.toString()
                    }
                    if(size > 3){
                        StepsProgressBar4.progress = Helpers.calculatePercentage(stepsList[size-4].count, currentGoal.steps.toDouble())
                        countSteps4.text = stepsList[size-4].count.toString()
                    }
                    if(size > 4){
                        StepsProgressBar5.progress = Helpers.calculatePercentage(stepsList[size-5].count, currentGoal.steps.toDouble())
                        countSteps5.text = stepsList[size-5].count.toString()
                    }
                    if(size > 5){
                        StepsProgressBar6.progress = Helpers.calculatePercentage(stepsList[size-6].count, currentGoal.steps.toDouble())
                        countSteps6.text = stepsList[size-6].count.toString()
                    }
                    if(size > 6){
                        StepsProgressBar7.progress = Helpers.calculatePercentage(stepsList[size-7].count, currentGoal.steps.toDouble())
                        countSteps7.text = stepsList[size-7].count.toString()
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

    // todo in realta bisgnerebbe verificare che avessero la stessa data tipo combinandole tra loro. ma se li metto assieme tipo in UserRecords non vanno!!!!!
    private fun handleProgressBarClick(num : Int) {
        (activity as MainActivity).recordsViewModel.weeklySteps.observe( viewLifecycleOwner, Observer { stepsList ->
            if(stepsList.size >= num && stepsList.isNotEmpty()) {

                date.text = Helpers.formatDateToString(stepsList.last().date)
                CircularCountSteps.text = stepsList.last().count.toString()
                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                if (currentGoal != null) {
                    // Aggiorna le progress bar
                    CircularProgressBarSteps.progress = Helpers.calculatePercentage(stepsList[stepsList.size - num].count, currentGoal.steps.toDouble())
                }
            }
        })

        (activity as MainActivity).recordsViewModel.weeklyCalories.observe( viewLifecycleOwner, Observer { caloriesList ->
            if(caloriesList.size >= num && caloriesList.isNotEmpty()) {
                date.text = Helpers.formatDateToString(caloriesList.last().date)
                CircularCountCalories.text =caloriesList.last().count.toString()
                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                if (currentGoal != null) {
                    // Aggiorna le progress bar
                    CircularProgressBarCalories.progress = Helpers.calculatePercentage(caloriesList[caloriesList.size - num].count, currentGoal.calories.toDouble())
                }
            }
        })

        (activity as MainActivity).recordsViewModel.weeklyDistance.observe( viewLifecycleOwner, Observer { distanceList ->
            if(distanceList.size >= num && distanceList.isNotEmpty()) {
                date.text = Helpers.formatDateToString(distanceList.last().date)
                CircularCountDistance.text = distanceList.last().count.toString()
                val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
                if (currentGoal != null) {
                    // Aggiorna le progress bar
                    CircularProgressBarDistance.progress = Helpers.calculatePercentage(distanceList[distanceList.size - num].count.toInt(), currentGoal.distance.toDouble())
                }
            }
        })

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


}


/*
 private fun getUserRecords(
        weeklySteps: Flow<List<Steps>>,
        weeklyCalories: Flow<List<Calories>>,
        weeklyDistances: Flow<List<Distance>>
    ): Flow<List<UserRecords>> {
        return combine(
            weeklySteps,
            weeklyCalories,
            weeklyDistances
        ) { stepsList, caloriesList, distancesList ->
            // Creiamo una lista di UserRecords.
            val userRecordsList = mutableListOf<UserRecords>()

            // Iteriamo su ogni elemento delle liste.
            for (i in stepsList.indices) {
                val steps = stepsList[i]
                val calories = caloriesList[i]
                val distance = distancesList[i]

                // Controlliamo se tutte le registrazioni appartengono allo stesso utente e hanno la stessa data.
                if (steps.userId == calories.userId && steps.userId == distance.userId &&
                    steps.date == calories.date && steps.date == distance.date
                ) {
                    userRecordsList.add(UserRecords(steps.userId, steps, calories, distance))
                }
            }

            userRecordsList
        }
 */