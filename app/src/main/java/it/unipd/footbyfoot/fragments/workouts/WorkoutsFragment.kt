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
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R

class WorkoutsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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

        var totd :Int =0
        var tott: Long =0
        (activity as MainActivity).recordsViewModel.totalDistance.observe(activity as MainActivity) { records ->
            totd = 0
            records?.let {
                totd= totd+it
            }
        }
        (activity as MainActivity).recordsViewModel.totalTime.observe(activity as MainActivity) { records ->
            tott = 0
            records?.let {
                tott= tott+it
            }
        }

        firebaseAnalytics = Firebase.analytics
        var avg: Double = totd/tott.toDouble()
        try{
            firebaseAnalytics.setUserProperty("Average speed", avg.toString())
        }catch (e: ArithmeticException){}

        return view
    }
}