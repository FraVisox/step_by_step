package com.example.room.fragments.steps

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


class MonthlyStepsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_monthly_steps, container, false)

        // Initialize the RecyclerView

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)

        // Observe LiveData from ViewModel
        (activity as MainActivity).recordsViewModel.monthlySteps.observe(viewLifecycleOwner, Observer { steps ->
            (activity as MainActivity).recordsViewModel.monthlyCalories.observe(viewLifecycleOwner, Observer { calories ->
                (activity as MainActivity).recordsViewModel.monthlyDistance.observe(viewLifecycleOwner, Observer { distances ->
                    val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }

                    if (currentGoal != null) {
                        val adapter = RecordsAdapter2(currentGoal, steps, calories, distances)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                    }


                })

            })

        })

        return view
    }

}


/*
 // Initialize the RecyclerView
            val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)
            val adapter = RecordsAdapterPrimo((activity as MainActivity).recordsViewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)


            // Observe LiveData from ViewModel

            (activity as MainActivity).recordsViewModel.monthlySteps.observe(viewLifecycleOwner, Observer { steps ->
                steps?.let { adapter.submitList(it) }
            })

            return view
 */