package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication

class WorkoutsFragment : Fragment() {

    //Firebase
    private var totD: Int = 0
    private var totT: Long = 0
    private var counter: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_info, container, false)

        //Initialize the button to add a custom workout
        val button = view.findViewById<AppCompatImageButton>(R.id.addWorkout)
        button.setOnClickListener {
            startActivity(Intent(activity, AddWorkoutActivity::class.java))
        }

        // Initialize the RecyclerView
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview_workouts)
        val adapter = WorkoutsAdapter(activity as MainActivity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //Observe LiveData from ViewModel and submit them to the adapter: this is done both for the workouts list and for the points list
        (activity as MainActivity).recordsViewModel.allWorkouts.observe(activity as MainActivity) { records ->
            records?.let {
                adapter.submitList(it)
            }
        }
        (activity as MainActivity).recordsViewModel.allPoints.observe(activity as MainActivity) { records ->
            records?.let {
                adapter.updatePoints(it)
            }
        }

        return view
    }

    override fun onPause() {
        //Register the number of workouts
        (activity as MainActivity).recordsViewModel.countWorkout.observe(activity as MainActivity) { record ->
            counter = record
        }

        //TODO: il numero di workout in questo modo viene preso solo se apre questo fragment. METTI IN MAIN??
        RecordsApplication.firebaseAnalytics.setUserProperty("Workouts counter", counter.toString())

        //Register the average speed
        (activity as MainActivity).recordsViewModel.totalDistance.observe(activity as MainActivity) { records ->
            totD = 0
            records?.let {
                totD += it
            }
            sendAverageSpeed()
        }
        (activity as MainActivity).recordsViewModel.totalTime.observe(activity as MainActivity) { records ->
            totT = 0
            records?.let {
                totT += it
            }
            sendAverageSpeed()
        }

        super.onPause()
    }

    private fun sendAverageSpeed() {
        if (totT != 0L) {
            val avg: Double = totD / totT.toDouble()
            //TODO: e anche la velocità
            RecordsApplication.firebaseAnalytics.setUserProperty("Average speed", avg.toString())
        }
    }
}