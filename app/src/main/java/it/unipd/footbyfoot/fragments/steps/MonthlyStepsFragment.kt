package it.unipd.footbyfoot.fragments.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R


class MonthlyStepsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monthly_steps, container, false)
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)

        (activity as MainActivity).recordsViewModel.last30Distances.observe(viewLifecycleOwner, Observer { distances ->

            val currentGoal = (activity as MainActivity).recordsViewModel.userGoal.value?.find { it.userId == 1 }
            val currentUser = (activity as MainActivity).recordsViewModel.allUsers.value?.find { it.userId == 1 }

            if(currentUser != null && currentGoal != null){
                val adapter = RecordsAdapter(currentGoal, currentUser, distances.reversed())
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(context)
            }
        })

        return view
    }

}

