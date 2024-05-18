package com.example.room.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.room.RecordsApplication
import com.example.room.R
import com.example.room.database.RecordsViewModel
import com.example.room.database.RecordsViewModelFactory


class MonthlyStepsFragment : Fragment() {

    class TodayStepsFragment : Fragment() {

        //private lateinit var activityViewModel: RecordsViewModel

        // Inizializza il ViewModel usando WordViewModelFactory per iniettare il repository necessario.
        private val recordsViewModel: RecordsViewModel by viewModels {
            RecordsViewModelFactory((requireActivity().application as RecordsApplication).repository)
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_show_weekly_steps, container, false)

            // Initialize the RecyclerView
            val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)
            val adapter = RecordsAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)


            // Observe LiveData from ViewModel
            recordsViewModel.monthlyUserActivities.observe(viewLifecycleOwner, Observer { records ->
                records?.let { adapter.submitList(it) }
            })

            return view
        }

    }

    // Funzione per popolare il database e stampare il suo contenuto nel logcat


}

// Initialize ViewModel
//activityViewModel = ViewModelProvider(this, RecordsViewModelFactory((activity?.application as RecordsApplication).repository)).get( RecordsViewModel::class.java)