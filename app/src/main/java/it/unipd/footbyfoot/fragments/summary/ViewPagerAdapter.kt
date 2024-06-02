package it.unipd.footbyfoot.fragments.summary

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        const val numberOfTabs = 3
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodaySummaryFragment()
            1 -> WeeklySummariesFragment()
            2 -> AllSummariesFragment()
            else -> TodaySummaryFragment()
        }
    }

    //Total number of tabs
    override fun getItemCount(): Int = numberOfTabs
}