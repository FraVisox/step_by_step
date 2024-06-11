package it.unipd.footbyfoot.fragments.workouts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R

class WorkoutsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_info, container, false)

        //Initialize the button to add a custom workout
        val fab = view.findViewById<FloatingActionButton>(R.id.addWorkout)
        fab.setOnClickListener {
            startActivity(Intent(activity, AddWorkoutActivity::class.java))
        }

        // Initialize the RecyclerView
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview_workouts)
        val adapter = WorkoutsAdapter(activity as MainActivity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //Observe LiveData from ViewModel and submit them to the adapter
        (activity as MainActivity).recordsViewModel.allWorkouts.observe(activity as MainActivity) { records ->
            records?.let {
                adapter.submitList(it)
            }
        }

        return view
    }
}