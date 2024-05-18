package com.example.room.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.room.R
import com.example.room.RecordsApplication
import com.example.room.database.RecordsViewModel
import com.example.room.database.RecordsViewModelFactory

class WeeklyStepsFragment : Fragment() {

    private val recordsViewModel: RecordsViewModel by viewModels {
        RecordsViewModelFactory((requireActivity().application as RecordsApplication).repository)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_weekly_steps, container, false)

        val progressBarStepsMon = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekMon)
        val progressBarStepsTues = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekTues)
        val progressBarStepsWed = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekWed)
        val progressBarStepsThur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekThurs)
        val progressBarStepsFri = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekFri)
        val progressBarStepsSatur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSatur)
        val progressBarStepsSun = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSun)

        val countStepsMon = view.findViewById<TextView>(R.id.dayMon)
        val countStepsTues = view.findViewById<TextView>(R.id.dayTues)
        val countStepsWed = view.findViewById<TextView>(R.id.dayWed)
        val countStepsThur = view.findViewById<TextView>(R.id.dayThur)
        val countStepsFri = view.findViewById<TextView>(R.id.dayFri)
        val countStepsSatur = view.findViewById<TextView>(R.id.daySat)
        val countStepsSun = view.findViewById<TextView>(R.id.daySun)

        recordsViewModel.todayUserActivities.observe(viewLifecycleOwner, Observer { records ->
            records?.let {
                // In realtà qui è solo uno
                records.forEach { record ->
                    val countS = record.steps.count.toString()
                    val countD = record.distance.count.toString()
                    val countC = record.calories.count.toString()

                    // Bisogna mettere quelli selezionati dal bro
                    val stepsGoal = 8004
                    val distanceGoal = 1204
                    val caloriesGoal = 1204

                    // Determina il giorno della settimana
                    val dayOfWeek =  Helpers.getDayOfWeek(record.steps.date)

                    when (dayOfWeek) {
                        "Monday" -> {
                            countStepsMon.text = countS
                            progressBarStepsMon.progress = Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Tuesday" -> {
                            countStepsTues.text = countS
                            progressBarStepsTues.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Wednesday" -> {
                            countStepsWed.text = countS
                            progressBarStepsWed.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Thursday" -> {
                            countStepsThur.text = countS
                            progressBarStepsThur.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Friday" -> {
                            countStepsFri.text = countS
                            progressBarStepsFri.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Saturday" -> {
                            countStepsSatur.text = countS
                            progressBarStepsSatur.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                        "Sunday" -> {
                            countStepsSun.text = countS
                            progressBarStepsSun.progress =  Helpers.calculatePercentage(countS.toInt(), stepsGoal)
                        }
                    }
                }
            }
        })


        return view
    }

}
/*
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    tools:context=".fragments.steps.TodayStepsFragment">
    <GridLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:columnCount="7"
        android:rowCount="2"
        android:orientation="horizontal"
        android:gravity="center">

        <RelativeLayout
            android:id="@+id/progress_monday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="16dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekMon"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekMon"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekMon"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>

        <RelativeLayout
            android:id="@+id/progress_tuesday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekTues"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekTues"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekTues"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>
        <RelativeLayout
            android:id="@+id/progress_wednesday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekWednes"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekWednes"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekWednes"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>
        <RelativeLayout
            android:id="@+id/progress_thursday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekThurs"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekThurs"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekThurs"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>
        <RelativeLayout
            android:id="@+id/progress_friday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekFri"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekFri"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekFri"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>
        <RelativeLayout
            android:id="@+id/progress_saturday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekSatur"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekSatur"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekSatur"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>
        <RelativeLayout
            android:id="@+id/progress_sunday_layout"
            android:layout_width="40dp"
            android:layout_height="300dp"
            android:layout_gravity="center"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp">

            <ProgressBar
                android:id="@+id/progressbarStepsWeekSun"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/rectangle_shape"
                android:indeterminate="false"
                android:progressDrawable="@drawable/rectangle_progressbar"
                android:textAlignment="center" />

            <TextView
                android:id="@+id/countStepsWeekSun"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:gravity="center"
                android:layout_above="@id/progressbarStepsWeekSun"
                android:textColor="@color/black"
                android:textSize="28sp"
                android:textStyle="bold"/>

        </RelativeLayout>
    </GridLayout>
</LinearLayout>
 */