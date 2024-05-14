package com.example.room.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.room.ActivityApplication
import com.example.room.R
import com.example.room.database.ActivityViewModel
import com.example.room.database.ActivityViewModelFactory
import com.example.room.database.ui.DailyRecordsAdapter
import androidx.lifecycle.Observer

class TodayStepsFragment : Fragment() {

    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        // Initialize the RecyclerView
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)
        val adapter = DailyRecordsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)


        // Initialize ViewModel
        activityViewModel = ViewModelProvider(this, ActivityViewModelFactory((activity?.application as ActivityApplication).repository)).get(
            ActivityViewModel::class.java)

        // Observe LiveData from ViewModel
        activityViewModel.todayUserActivities.observe(viewLifecycleOwner, Observer { records ->
            records?.let { adapter.submitList(it) }
        })

        return view
    }

}