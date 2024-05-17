package com.example.room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SetNewGoalsActivity : AppCompatActivity() {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)


        stepsGoal = findViewById(R.id.stepsGoalCount)
        caloriesGoal = findViewById(R.id.caloriesGoalCount)
        distanceGoal = findViewById(R.id.distanceGoalCount)

        addStepsButton = findViewById(R.id.addStepsButton)
        subStepsButton = findViewById(R.id.subStepsButton)
        addCaloriesButton = findViewById(R.id.addCaloriesButton)
        subCaloriesButton = findViewById(R.id.subCaloriesButton)
        addDistanceButton = findViewById(R.id.addDistanceButton)
        subDistanceButton = findViewById(R.id.subDistanceButton)


        val preferences = getPreferences(MODE_PRIVATE)
        stepsGoal.text = preferences.getInt(STEPS_GOAL, 0).toString()
        caloriesGoal.text = preferences.getInt(CALORIES_GOAL, 0).toString()
        distanceGoal.text = preferences.getInt(DISTANCE_GOAL, 0).toString()


        addStepsButton.setOnClickListener { increment100Goal(stepsGoal) }
        subStepsButton.setOnClickListener { decrement100Goal(stepsGoal) }
        addCaloriesButton.setOnClickListener { increment100Goal(caloriesGoal) }
        subCaloriesButton.setOnClickListener { decrement100Goal(caloriesGoal) }
        addDistanceButton.setOnClickListener { incrementGoal(distanceGoal) }
        subDistanceButton.setOnClickListener { decrementGoal(distanceGoal) }
    }

    override fun onPause() {
        super.onPause()
        val preferences = getPreferences(MODE_PRIVATE)
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