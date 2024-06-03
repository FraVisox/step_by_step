package it.unipd.footbyfoot


import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.databinding.ActivityMainBinding
import it.unipd.footbyfoot.fragments.goals.GoalsFragment
import it.unipd.footbyfoot.fragments.maps.MapsFragment
import it.unipd.footbyfoot.fragments.settings.SettingsFragment
import it.unipd.footbyfoot.fragments.summary.SummaryFragment
import it.unipd.footbyfoot.fragments.workouts.WorkoutsFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class MainActivity : AppCompatActivity() {

    // Firebase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    //Current fragment and data binding
    private lateinit var binding : ActivityMainBinding
    private var thisFragment : Int = R.id.stepsFragment

    //View model
    val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }

    companion object {
        //Key for the fragment of instance state
        private const val fragment = "currentFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = Firebase.analytics

        if (savedInstanceState != null) {
            binding.bottomNavigationView.post {
                binding.bottomNavigationView.selectedItemId = savedInstanceState.getInt(fragment)
            }
        } else {
            binding.bottomNavigationView.post {
                binding.bottomNavigationView.selectedItemId = R.id.BottomBarSummary
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.BottomBarSummary -> replaceFragment(R.id.stepsFragment, SummaryFragment::class.qualifiedName)
                R.id.BottomBarActivity -> replaceFragment(R.id.mapsFragment, MapsFragment::class.qualifiedName)
                R.id.BottomBarWorkouts -> replaceFragment(R.id.allenamentiFragment, WorkoutsFragment::class.qualifiedName)
                R.id.BottomBarGoals -> replaceFragment(R.id.goalsFragment, GoalsFragment::class.qualifiedName)
                R.id.BottomBartUserSettings -> replaceFragment(R.id.settingsFragment, SettingsFragment::class.qualifiedName)
                else ->{

                }
            }
            true
        }
    }

    //Replace the fragment
    private fun replaceFragment(fragmentId : Int, classname : String?){

        val tag = fragmentId.toString()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.primaryNavigationFragment

        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment)
        }

        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = fragmentManager.fragmentFactory.instantiate(this.classLoader, classname!!)
            fragmentTransaction.add(R.id.activity_main_nav_host_fragment, fragment, tag)
        } else {
            fragmentTransaction.attach(fragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNow()

        thisFragment = fragmentId
    }

    //Save the instance state, which means which fragment is selected
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(fragment, thisFragment)
    }

    //Show permissions dialog for location
    fun showLocationDialog(exception: ResolvableApiException) {
        AlertDialog.Builder(this)
            .setMessage(
                R.string.enable_location
            )
            .setPositiveButton(
                getString(R.string.show_dialog)
            ) { _,_ ->
                exception.startResolutionForResult(this, 1)
            }.setNegativeButton(
                getString(R.string.ignore_dialog)
            ) { _, _ ->
            }
            .create().show()
    }
}