package com.example.room.fragments.maps

import android.Manifest
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.room.R
import com.google.android.gms.maps.SupportMapFragment

//TO ADD MY LOCATION: https://developers.google.com/android/reference/com/google/android/gms/location/package-summary
class FinishActivityFragment : Fragment() {

    //Create the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.finish_activity_maps, container, false)

        //val time = view.findViewById<TextView>(R.id.time_tv)
        /*let {
            time.text = Calendar.getInstance().timeInMillis.toString()
        }

         */

        val finish = view.findViewById<Button>(R.id.finish_button)
        finish.setOnClickListener {
            (requireParentFragment().parentFragment as MapsFragment).manager.finishActivity()
            view.findNavController()
                .navigate(R.id.action_finishToStart)
        }

        return view
    }

}