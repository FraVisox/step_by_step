package com.example.room.fragments.steps

import android.content.Intent
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
import com.example.room.NewWordActivity
import com.example.room.SetNewGoalsActivity

class TodayStepsFragment : Fragment() {

    private lateinit var activityViewModel: ActivityViewModel
    val progressBarSteps : ProgressBar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_today_steps, container, false)

        /*
        rimettere in caso:
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

         <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"
        android:layout_margin="16dp"
        android:contentDescription="@string/add_word"
        android:src="@drawable/baseline_stars_24"/>



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
        */
        val progressBarSteps = view.findViewById<ProgressBar>(R.id.progressbarStepsToday)
        val countSteps = view.findViewById<TextView>(R.id.countStepsToday)

        progressBarSteps.setProgress(80)
        countSteps.setText("120")

        val progressBarColaries = view.findViewById<ProgressBar>(R.id.progressbarCaloriesToday)
        val countCalories = view.findViewById<TextView>(R.id.countCaloriesToday)

        progressBarColaries.setProgress(20)
        countCalories.setText("660")

        val progressBarDistance = view.findViewById<ProgressBar>(R.id.progressbarDistanceToday)
        val countDistance = view.findViewById<TextView>(R.id.countDistanceToday)

        progressBarDistance.setProgress(90)
        countDistance.setText("5")

        val goals : TextView = view.findViewById(R.id.goalsStepsToday)
        goals.setOnClickListener {
            val intent = Intent(view.context,SetNewGoalsActivity::class.java)
            view.context.startActivity(intent)
        }



        return view
    }

}