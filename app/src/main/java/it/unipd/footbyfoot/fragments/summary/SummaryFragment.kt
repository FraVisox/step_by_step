package it.unipd.footbyfoot.fragments.summary

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

class SummaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("AAA", "summary fragment created")
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        //TODO: prendi da stringhe
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Last"
                1 -> tab.text = "Last 7"
                2 -> tab.text = "Last 30"
                else -> tab.text = "Last"
            }
        }.attach()

    }
}

