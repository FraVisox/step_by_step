package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R

class WorkoutsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.workout_fragment, container, false)

        //Initialize the button
        val button = view.findViewById<AppCompatImageButton>(R.id.addWorkout)
        button.setOnClickListener {
            startActivity(Intent(activity, AddWorkoutActivity::class.java))
        }

        // Initialize the RecyclerView
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview_workouts)
        val adapter = WorkoutsAdapter(activity as MainActivity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Observe LiveData from ViewModel and submit them to the adapter: this is done both for the workouts list and for the points list
        (activity as MainActivity).recordsViewModel.allWorkouts.observe(activity as MainActivity, Observer { records ->
            records?.let {
                adapter.submitList(it)
            }
        })
        (activity as MainActivity).recordsViewModel.allPoints.observe(activity as MainActivity, Observer { records ->
            records?.let {
                adapter.updatePoints(it)
            }
        })

        return view
    }


}