package it.unipd.footbyfoot.fragments.summary


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodaySummaryFragment()
            1 -> WeeklySummariesFragment()
            2 -> AllSummariesFragment()
            else -> TodaySummaryFragment()  // Default case
        }
    }

    override fun getItemCount(): Int = 3  // Numero totale di tab
}