package it.unipd.footbyfoot


import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.databinding.ActivityMainBinding
import it.unipd.footbyfoot.fragments.goals.GoalsFragment
import it.unipd.footbyfoot.fragments.maps.MapsFragment
import it.unipd.footbyfoot.fragments.settings.SettingsFragment
import it.unipd.footbyfoot.fragments.summary.SummaryFragment
import it.unipd.footbyfoot.fragments.workouts.WorkoutsFragment
import com.google.android.gms.common.api.ResolvableApiException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var thisFragment : Int = R.id.stepsFragment

    val recordsViewModel : RecordsViewModel by viewModels{
        (application as RecordsApplication).viewModelFactory
    }


    fun showLocationDialog(exception: ResolvableApiException) {
        //Show permissions dialog
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            //TODO: bug se tolgo le autorizzazioni mentre sono in maps o altra parte
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

        // todo bug enorme se lo tolgo non va niente !!
        recordsViewModel.allDistances.observe(this, Observer { records ->
            if (records.isNotEmpty()) {
                // Converti la lista in una stringa leggibile
                // Logga il contenuto della lista
            } else {
                Log.d("today", "La lista dei records di oggi è vuota")
            }
        })


    }

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
            //Questo ci permette di crearlo una sola volta, così lo stato rimane se passo da una parte all'altra
            fragment = fragmentManager.fragmentFactory.instantiate(this.classLoader, classname!!)
            fragmentTransaction.add(R.id.activity_main_nav_host_fragment, fragment, tag)
        } else {
            fragmentTransaction.attach(fragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNow()

        thisFragment = fragmentId
        //fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(fragment, thisFragment)
    }

    companion object {
        private const val fragment = "currentFragment"
        const val currentID = "workoutID"
    }
}