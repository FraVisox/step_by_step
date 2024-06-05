package it.unipd.footbyfoot.fragments.summary

import android.os.Bundle
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
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Take view pager and table layout
        val viewPager : ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout : TabLayout = view.findViewById(R.id.tabs)

        //Set adapter
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        //And table layout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.today)
                1 -> tab.text = getString(R.string.week)
                2 -> tab.text = getString(R.string.all)
                else -> tab.text = getString(R.string.today)
            }
        }.attach()

    }
}

