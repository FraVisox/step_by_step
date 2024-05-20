package com.example.room.fragments.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.room.R

class StartWorkoutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.start_activity_maps, container, false)

        val start = view.findViewById<Button>(R.id.start_button)
        start.setOnClickListener {
            (requireParentFragment().parentFragment as MapsFragment).manager.startActivity()
            view.findNavController()
                .navigate(R.id.action_startToFinish)
        }

        return view
    }
}