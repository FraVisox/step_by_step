package com.example.room.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.room.R
import com.example.room.database.ActivityViewModel

class WeeklyStepsFragment : Fragment() {

    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_weekly_steps, container, false)

        /*
        in caso da mettere se ripristini
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
        activityViewModel.weeklyUserActivities.observe(viewLifecycleOwner, Observer { records ->
            records?.let { adapter.submitList(it) }
        })
         */

        val progressBarStepsMon = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekMon)
        progressBarStepsMon.setProgress(20)

        val progressBarStepsTues = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekTues)
        progressBarStepsTues.setProgress(50)

        val progressBarStepsWed = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekWed)
        progressBarStepsWed.setProgress(100)

        val progressBarStepsThur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekThurs)
        progressBarStepsThur.setProgress(10)

        val progressBarStepsFri = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekFri)
        progressBarStepsFri.setProgress(80)


        val progressBarStepsSatur = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSatur)
        progressBarStepsSatur.setProgress(80)

        val progressBarStepsSun = view.findViewById<ProgressBar>(R.id.progressbarStepsWeekSun)
        progressBarStepsSun.setProgress(90)






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