package it.unipd.footbyfoot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.databinding.ActivityMainBinding
import it.unipd.footbyfoot.fragments.goals.GoalsFragment
import it.unipd.footbyfoot.fragments.maps.MapsFragment
import it.unipd.footbyfoot.fragments.settings.SettingsFragment
import it.unipd.footbyfoot.fragments.summary.SummaryFragment
import it.unipd.footbyfoot.fragments.workouts.WorkoutsFragment
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.database.RecordsViewModelFactory
import it.unipd.footbyfoot.fragments.maps.TrackWorkoutService
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //Firebase
    lateinit var firebaseAnalytics: FirebaseAnalytics

    //Firebase values for user properties
    private var totD: Int = 0
    private var totT: Long = 0

    //Current fragment and data binding
    private lateinit var binding : ActivityMainBinding
    private var thisFragment : Int = R.id.stepsFragment

    //View model
    val recordsViewModel : RecordsViewModel by viewModels{
        RecordsViewModelFactory((application as RecordsApplication).repository)
    }

    //Add a callback to all the startActivityForResult used
    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultListener(this)
    )

    companion object {
        //Key for the fragment of instance state
        const val fragment = "currentFragment"
        const val firstUse = "first"

        //Firebase const for event
        const val servicePausedKey = "service_paused"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //Bind
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize firebase
        firebaseAnalytics = Firebase.analytics

        //If the user didn't accept to send data to Firebase, show a dialog
        val preferences = getPreferences(MODE_PRIVATE)
        if (preferences.getBoolean(firstUse, true) && savedInstanceState == null) {
            val dialog = PermissionDialog()
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, getString(R.string.permission_dialog))
        }

        //OBSERVE:
        //Changes in workout counts
        recordsViewModel.countWorkout.observe(this) { record ->
            firebaseAnalytics.setUserProperty(RecordsApplication.workoutCounter, record.toString())
        }
        //Changes in average speed
        recordsViewModel.totalDistance.observe(this) { records ->
            totD = 0
            records?.let {
                totD += it
            }
            setAverageSpeed()
        }
        recordsViewModel.totalTime.observe(this) { records ->
            totT = 0
            records?.let {
                totT += it
            }
            setAverageSpeed()
        }

        //Set listeners for the bottom bar
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.BottomBarSummary -> replaceFragment(R.id.stepsFragment, SummaryFragment::class.qualifiedName)
                R.id.BottomBarActivity -> replaceFragment(R.id.mapsFragment, MapsFragment::class.qualifiedName)
                R.id.BottomBarWorkouts -> replaceFragment(R.id.allenamentiFragment, WorkoutsFragment::class.qualifiedName)
                R.id.BottomBarGoals -> replaceFragment(R.id.goalsFragment, GoalsFragment::class.qualifiedName)
                R.id.BottomBartUserSettings -> replaceFragment(R.id.settingsFragment, SettingsFragment::class.qualifiedName)
                else ->{}
            }
            true
        }

        //Restore state by selecting the correct fragment
        if (savedInstanceState != null) {
            attachFragment(savedInstanceState.getInt(fragment))
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.BottomBarSummary
        }
     }

    //Attach current fragment
    private fun attachFragment(fragmentId : Int){

        val tag = fragmentId.toString()

        val fragmentManager = supportFragmentManager

        //Detach the fragment that is now displayed
        val fragmentTransaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.primaryNavigationFragment

        if (currentFragment != null) {
            //Detaching it means to allow to restore its state more efficiently
            fragmentTransaction.detach(currentFragment)
        }

        //Search if the fragment has already been created
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            //Else, attach it
            fragmentTransaction.attach(fragment)
        }

        //Update current fragment
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNow()

        thisFragment = fragmentId
    }

    //Replace the fragment
    private fun replaceFragment(fragmentId : Int, classname : String?){

        val tag = fragmentId.toString()

        val fragmentManager = supportFragmentManager

        //The current fragment is saved in primaryNavigationFragment, if present
        val fragmentTransaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.primaryNavigationFragment

        if (currentFragment != null) {
            //Detaching it means to allow to restore its state more efficiently
            fragmentTransaction.detach(currentFragment)
        }

        //Search if the fragment has already been created
        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            //If not, create it
            fragment = fragmentManager.fragmentFactory.instantiate(this.classLoader, classname!!)
            fragmentTransaction.add(R.id.activity_main_nav_host_fragment, fragment, tag)
        } else {
            //Else, attach it
            fragmentTransaction.attach(fragment)
        }

        //Update current fragment
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

    /*
     * FIREBASE EVENTS
     */
    //Function called when the application is in background but the user clicks on notification
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (TrackWorkoutService.running) {
            val bundle = Bundle()
            bundle.putBoolean(servicePausedKey, TrackWorkoutService.paused)
            firebaseAnalytics.logEvent(RecordsApplication.openedWithNotification, bundle)
        }
    }

    //Function called to change speed as user property
    private fun setAverageSpeed() {
        if (totT != 0L) {
            val avg: Double = totD / totT.toDouble()
            firebaseAnalytics.setUserProperty(RecordsApplication.averageSpeed, "%.2f".format(Locale.US, avg))
        }
    }

}