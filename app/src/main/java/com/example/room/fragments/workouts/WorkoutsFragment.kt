package com.example.room.fragments.workouts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.room.MainActivity
import com.example.room.R

class WorkoutsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.workout_fragment, container, false)
        // Initialize the RecyclerView
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview_workouts)
        val adapter = WorkoutsAdapter(activity as MainActivity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Observe LiveData from ViewModel
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