package com.example.room.fragments.maps.bottomfragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.room.R
import com.example.room.fragments.maps.MapsFragment
import com.example.room.fragments.maps.MapsFragment.Companion.workoutStarted
import com.example.room.fragments.maps.manager.MapsManager

class FinishWorkoutFragment : Fragment() {

    private lateinit var fragment: MapsFragment
    private lateinit var timeView : TextView
    private lateinit var distanceView : TextView

    private var paused = false

    private val pauseListener = OnClickListener { v ->
        if (paused) {
            fragment.manager.restartWorkout()
            (v as Button).text = getString(R.string.pause_workout)
            paused = false
        } else {
            fragment.manager.pauseWorkout()
            (v as Button).text = getString(R.string.restart_workout)
            paused = true
        }
    }

    //Create the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.maps_finish_workout, container, false)

        fragment = (parentFragment?.parentFragment as MapsFragment)

        //Initialize the text views
        timeView = view.findViewById(R.id.time_tv)
        distanceView = view.findViewById(R.id.km_tv)

        //Start the activity
        if (activity?.getPreferences(Context.MODE_PRIVATE)?.getBoolean(workoutStarted, false) == true) {
            fragment.manager.restartWorkout(timeView, distanceView)
        } else {
            if (!fragment.manager.startWorkout(timeView, distanceView)) {
                Toast.makeText(context, "Position not found", Toast.LENGTH_SHORT).show()
                findNavController()
                    .navigate(R.id.action_finishToStart)
            }
        }

        val finish = view.findViewById<Button>(R.id.finish_button)
        finish.setOnClickListener {
            fragment.manager.finishWorkout()
            view.findNavController()
                .navigate(R.id.action_finishToStart)
        }

        val pause = view.findViewById<Button>(R.id.pause_button)
        pause.setOnClickListener(pauseListener)

        return view
    }

    override fun onPause() {
        super.onPause()
        fragment.manager.pauseWorkout()

        //As the workout could work between different stages of the application, we save it in shared preferences
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.putBoolean(workoutStarted, true)
        editor?.apply()
    }
}