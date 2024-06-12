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
import it.unipd.footbyfoot.RecordsApplication

class AllSummariesFragment : Fragment() {

    //Personalized trace
    private val monthTrace = Firebase.performance.newTrace(RecordsApplication.allSummariesTrace)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_summaries, container, false)
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)

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

    //FIREBASE TRACE
    override fun onResume() {
        super.onResume()
        //Start trace
        monthTrace.start()
    }
    override fun onPause(){
        //Stop trace
        monthTrace.stop()
        super.onPause()
    }

}

