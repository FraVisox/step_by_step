package it.unipd.footbyfoot.fragments.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R

class AllSummariesFragment : Fragment() {

    //Personalized trace
    private val monthTrace = Firebase.performance.newTrace("Month_trace")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_summaries, container, false)
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)

        //Start trace
        monthTrace.start()
        start = System.currentTimeMillis()

        //Create adapter
        val adapter = SummariesAdapter(requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        //Observe
        (activity as MainActivity).recordsViewModel.allDistances.observe(viewLifecycleOwner) { distances ->
            adapter.submitList(distances)
        }

        (activity as MainActivity).recordsViewModel.allGoals.observe(viewLifecycleOwner) {goals ->
            adapter.submitGoals(goals)
        }

        (activity as MainActivity).recordsViewModel.allInfo.observe(viewLifecycleOwner) { info ->
            adapter.submitInfo(info)
        }

        return view
    }

    override fun onPause(){
        monthTrace.stop()   //Stop trace

        super.onPause()
    }

}

