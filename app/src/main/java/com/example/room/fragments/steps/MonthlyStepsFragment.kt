package com.example.room.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.room.ActivityApplication
import com.example.room.R
import com.example.room.database.ActivityViewModel
import com.example.room.database.ActivityViewModelFactory
import com.example.room.database.ui.DailyRecordsAdapter
import androidx.lifecycle.Observer
class MonthlyStepsFragment : Fragment() {

    class TodayStepsFragment : Fragment() {

        private lateinit var activityViewModel: ActivityViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_weekly_steps, container, false)

        /*
        <?xml version="1.0" encoding="utf-8"?>
        <androidx.constraintlayout.widget.ConstraintLayout
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            xmlns:tools="http://schemas.android.com/tools"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            tools:context=".fragments.steps.TodayStepsFragment">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerview"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:padding="@dimen/big_padding"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.0"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.0"
            tools:listitem="@layout/daily_records_item" />
        </androidx.constraintlayout.widget.ConstraintLayout>

            // Initialize the RecyclerView
            val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)
            val adapter = DailyRecordsAdapter()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)


            // Initialize ViewModel
            activityViewModel = ViewModelProvider(this, ActivityViewModelFactory((activity?.application as ActivityApplication).repository)).get(
                ActivityViewModel::class.java)

            // Observe LiveData from ViewModel
            activityViewModel.monthlyUserActivities.observe(viewLifecycleOwner, Observer { records ->
                records?.let { adapter.submitList(it) }
            })
            */

            return view
        }

    }

}