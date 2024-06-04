package it.unipd.footbyfoot.fragments.maps.bottomfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.navigation.findNavController
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.maps.MapsFragment
import it.unipd.footbyfoot.fragments.maps.TrackWorkoutService

class FinishWorkoutFragment : Fragment() {

    //References to the parent fragment and the views to update
    private lateinit var fragment: MapsFragment
    private lateinit var timeView : Chronometer
    private lateinit var distanceView : TextView

    //Create the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps_bottom_finish, container, false)

        //Initialize the properties
        fragment = (parentFragment?.parentFragment as MapsFragment)
        timeView = view.findViewById(R.id.time_chrono)
        distanceView = view.findViewById(R.id.km_tv)

        timeView.visibility = View.INVISIBLE

        //Start the workout and set the views
        fragment.manager.startWorkout(timeView, distanceView)

        //Set listeners
        val finish = view.findViewById<Button>(R.id.finish_button)
        finish.setOnClickListener {
            fragment.manager.stopWorkout()
            view.findNavController()
                .navigate(R.id.action_finishToStart)
        }

        val pause = view.findViewById<Button>(R.id.pause_button)
        pause.setOnClickListener{ v ->
            if (TrackWorkoutService.running && TrackWorkoutService.paused) {
                fragment.manager.resumeWorkout()
                (v as Button).text = getString(R.string.pause_workout)
            } else if (TrackWorkoutService.running) {
                fragment.manager.pauseWorkout()
                (v as Button).text = getString(R.string.resume_workout)
            }
        }

        //Restore state: as the service is running if the workout is in progress, it's his state that defines the state
        if (TrackWorkoutService.running && TrackWorkoutService.paused) {
            pause.text = getString(R.string.resume_workout)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (TrackWorkoutService.running) {
            fragment.manager.takeOnWorkout()
        }
    }
}