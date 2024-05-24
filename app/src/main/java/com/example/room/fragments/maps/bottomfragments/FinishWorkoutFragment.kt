package com.example.room.fragments.maps.bottomfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.room.R
import com.example.room.fragments.maps.MapsFragment

class FinishWorkoutFragment : Fragment() {

    private lateinit var fragment: MapsFragment
    private lateinit var timeView : TextView
    private lateinit var distanceView : TextView


    //Create the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.maps_finish_workout, container, false)
        Log.d("AAA", "recreated maps bottom ${savedInstanceState.toString()}")

        fragment = (parentFragment?.parentFragment as MapsFragment)

        //Initialize the text views
        timeView = view.findViewById(R.id.time_tv)
        distanceView = view.findViewById(R.id.km_tv)

        if (savedInstanceState != null) {
            fragment.manager.restartWorkout(timeView, distanceView, savedInstanceState.getLong(timeKey), savedInstanceState.getDouble(distanceKey), savedInstanceState.getInt(idKey))
        }

        //Start the activity
        if (!fragment.manager.startWorkout(timeView, distanceView)) {
            fragment.manager.restartWorkout(timeView, distanceView)
        }

        val finish = view.findViewById<Button>(R.id.finish_button)
        finish.setOnClickListener {
            fragment.manager.finishWorkout()
            view.findNavController()
                .navigate(R.id.action_finishToStart)
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        fragment.manager.pauseWorkout()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragment.manager.pauseWorkout()
        outState.putBoolean(workoutStarted, true)
    }

    companion object {
        const val workoutStarted = "workoutStarted" //TODO: mettilo solo dentro a maps
        const val distanceKey = "distance"
        const val timeKey = "time"
        const val idKey = "id"
    }

}