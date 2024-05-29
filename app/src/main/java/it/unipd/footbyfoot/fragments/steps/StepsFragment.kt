package it.unipd.footbyfoot.fragments.steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import it.unipd.footbyfoot.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StepsFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("AAA", "steps fragment created")
        return inflater.inflate(R.layout.fragment_steps, container, false)
    }
    // todo mettere i settings e i goals di default

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tabs)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Assegna i nomi alle tabs in base alla posizione
            when (position) {
                0 -> tab.text = "Last"
                1 -> tab.text = "Last 7"
                2 -> tab.text = "Last 30"
                else -> tab.text = "Last"
            }
        }.attach()

    }
}

