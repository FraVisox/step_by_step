package it.unipd.footbyfoot.fragments.summary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.settings.SettingsFragment


class AllSummariesFragment : Fragment() {

    //todo mettere in all metri al posto di kilometri ed invertire il tutto (ultima cosa in alto non in basso)
    // mettere in week il perform click sulla data di oggi.
    // fixare il today che non si vede niente rispetto al week e al goal
    // se cambio altezza cambiano tutti i dati? si
    // se cambio i goals?
    // in orizzontale se vado in settings si rompe tutto.
    // togliere un laout di allSummariesLand e itemLand (cardItem ha una piccola diversita quindi tenere)
    // io non ho potuto fare test per il maps perche non va il gps sul mio emulatore è tutto ok?
    // togliere i worning di lint sui layout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_summaries, container, false)
        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerview)

        //Get preferences
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val weight = preferences.getInt(SettingsFragment.WEIGHT, SettingsFragment.defaultWeight)
        val height = preferences.getInt(SettingsFragment.HEIGHT, SettingsFragment.defaultHeight)

        //Create adapter
        val adapter = SummariesAdapter(height, weight, requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        //Observe
        (activity as MainActivity).recordsViewModel.allDistances.observe(viewLifecycleOwner) { distances ->
            adapter.submitList(distances)
        }

        (activity as MainActivity).recordsViewModel.allGoals.observe(viewLifecycleOwner) {goals ->
            adapter.submitGoals(goals)
        }

        return view
    }

}

