package com.example.room.fragments.maps.bottomfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
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

        //Start the activity
        val res = fragment.manager.startWorkout(timeView, distanceView)
        if (res == MapsManager.STARTED) {
            Log.d("AAA", "restarted")
            fragment.manager.restartWorkout(timeView, distanceView)
        } else if (res == MapsManager.POS_NOT_FOUND) {
            Toast.makeText(context, "Position not found", Toast.LENGTH_SHORT).show()
            findNavController()
                .navigate(R.id.action_finishToStart)
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

}