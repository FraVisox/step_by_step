package it.unipd.footbyfoot.fragments.summary


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LastSummaryFragment()
            1 -> Last7SummariesFragment()
            2 -> Last30SummariesFragment()
            else -> LastSummaryFragment()  // Default case
        }
    }

    override fun getItemCount(): Int = 3  // Numero totale di tab
}