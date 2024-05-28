package it.unipd.footbyfoot.fragments.steps


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayStepsFragment()
            1 -> WeeklyStepsFragment()
            2 -> MonthlyStepsFragment()
            else -> TodayStepsFragment()  // Default case
        }
    }

    override fun getItemCount(): Int = 3  // Numero totale di tab
}