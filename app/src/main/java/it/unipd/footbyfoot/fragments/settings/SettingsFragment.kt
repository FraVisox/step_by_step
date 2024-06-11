package it.unipd.footbyfoot.fragments.settings

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.RecordsApplication
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

class SettingsFragment : Fragment() {

    // Class constants and default values
    companion object {
        internal const val WEIGHT = "weight"
        internal const val HEIGHT = "height"
        internal const val AGE = "age"
        const val defaultWeight = 60
        const val defaultHeight = 180
        const val defaultAge = 30

        //Firebase keys for event
        const val ageIncrementKey = "age_increment"
        const val ageDecrementKey = "age_decrement"
        const val heightIncrementKey = "height_increment"
        const val heightDecrementKey = "height_decrement"
        const val weightIncrementKey = "weight_increment"
        const val weightDecrementKey = "weight_decrement"
    }

    // Class text views
    private lateinit var ageSettings: TextView
    private var weightSettings: TextView? = null
    private var heightSettings: TextView? = null

    //Counters used for the events in firebase
    private var counterIncrementAge = 0
    private var counterIncrementHeight = 0
    private var counterIncrementWeight = 0
    private var counterDecrementAge = 0
    private var counterDecrementHeight = 0
    private var counterDecrementWeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        ageSettings = view.findViewById(R.id.ageCount)
        weightSettings = view.findViewById(R.id.weightCount)
        heightSettings = view.findViewById(R.id.heightCount)

        //Get saved age, weight and height. We take these values and not the ones in the database
        //as sometimes may happen that the inserting in the database and updating of livedata is
        //not instantaneous
        val preferences = requireActivity().getPreferences(MODE_PRIVATE)
        ageSettings.text = preferences.getInt(AGE, defaultAge).toString()
        weightSettings?.text = preferences.getInt(WEIGHT, defaultWeight).toString()
        heightSettings?.text = preferences.getInt(HEIGHT, defaultHeight).toString()

        //Listeners to buttons
        val addAgeButton: ImageButton = view.findViewById(R.id.addAgeButton)
        val subAgeButton: ImageButton = view.findViewById(R.id.subAgeButton)
        val addWeightButton: ImageButton = view.findViewById(R.id.addWeightButton)
        val subWeightButton: ImageButton = view.findViewById(R.id.subWeightButton)
        val addHeightButton: ImageButton = view.findViewById(R.id.addHeightButton)
        val subHeightButton: ImageButton = view.findViewById(R.id.subHeightButton)

        addAgeButton.setOnClickListener {
            Helpers.incrementValue(ageSettings)
            counterIncrementAge++
        }

        subAgeButton.setOnClickListener {
            Helpers.decrementValue(ageSettings)
            counterDecrementAge++
        }

        addWeightButton.setOnClickListener {
            Helpers.incrementValue(weightSettings)
            counterIncrementWeight++
        }

        subWeightButton.setOnClickListener {
            Helpers.decrementValue(weightSettings)
            counterDecrementWeight++
        }

        addHeightButton.setOnClickListener {
            Helpers.incrementValue(heightSettings)
            counterIncrementHeight++
        }

        subHeightButton.setOnClickListener {
            Helpers.decrementValue(heightSettings)
            counterDecrementHeight++
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        counterIncrementHeight = 0
        counterDecrementHeight = 0
        counterDecrementWeight = 0
        counterIncrementWeight = 0
        counterIncrementAge = 0
        counterDecrementAge = 0
    }

    override fun onPause() {
        super.onPause()

        insertInfo()

        //Save new current user properties
        (activity as MainActivity).firebaseAnalytics.setUserProperty(RecordsApplication.height, heightSettings?.text.toString())
        (activity as MainActivity).firebaseAnalytics.setUserProperty(RecordsApplication.weight, weightSettings?.text.toString())
        (activity as MainActivity).firebaseAnalytics.setUserProperty(RecordsApplication.declaredAge, ageSettings.text.toString())

        //Send event
        if (counterIncrementAge != 0 || counterDecrementAge != 0 || counterIncrementHeight != 0 ||
            counterDecrementHeight != 0 || counterIncrementWeight != 0 || counterDecrementWeight != 0) {
            val bundle = Bundle()
            bundle.putInt(ageIncrementKey, counterIncrementAge)
            bundle.putInt(ageDecrementKey, counterDecrementAge)
            bundle.putInt(heightIncrementKey, counterIncrementHeight)
            bundle.putInt(heightDecrementKey, counterDecrementHeight)
            bundle.putInt(weightIncrementKey, counterIncrementWeight)
            bundle.putInt(weightDecrementKey, counterDecrementWeight)
            (activity as MainActivity).firebaseAnalytics.logEvent(RecordsApplication.settingsUpdate, bundle)
        }
    }

    private fun insertInfo() {
        //Take new values
        val updatedHeight = heightSettings?.text.toString().toInt()
        val updatedWeight = weightSettings?.text.toString().toInt()
        val updatedAge = ageSettings.text.toString().toInt()

        val preferences = requireActivity().getPreferences(MODE_PRIVATE)
        //Get last values (to know if we need to update or not)
        val currentHeight = preferences.getInt(
            HEIGHT,
            defaultHeight
        )
        val currentWeight = preferences.getInt(WEIGHT, defaultWeight)

        //Insert age, weight and height on shared preferences
        val editor = preferences.edit()
        editor.putInt(AGE, updatedAge)
        editor.putInt(WEIGHT, updatedWeight)
        editor.putInt(HEIGHT, updatedHeight)
        editor.apply()

        //Insert a new info in the database only if it is different from the current one (as we don't want many similar data)
        if (currentHeight != updatedHeight || currentWeight != updatedWeight) {
            val date = LocalDate.now()
            val updatedInfo = UserInfo(
                date.year,
                date.dayOfYear,
                updatedHeight,
                updatedWeight
            )
            (activity as MainActivity).recordsViewModel.insertInfo(updatedInfo)
        }
    }
}