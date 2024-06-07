package it.unipd.footbyfoot.fragments.settings

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import it.unipd.footbyfoot.MainActivity
import it.unipd.footbyfoot.R
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
    }

    // Class text views
    private lateinit var ageSettings: TextView
    private var weightSettings: TextView? = null
    private var heightSettings: TextView? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseAnalytics = Firebase.analytics

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
        }

        subAgeButton.setOnClickListener {
            Helpers.decrementValue(ageSettings)
        }

        addWeightButton.setOnClickListener {
            Helpers.incrementValue(weightSettings)
        }

        subWeightButton.setOnClickListener {
            Helpers.decrementValue(weightSettings)
        }

        addHeightButton.setOnClickListener {
            Helpers.incrementValue(heightSettings)
        }

        subHeightButton.setOnClickListener {
            Helpers.decrementValue(heightSettings)
        }

        return view
    }

    override fun onPause() {
        super.onPause()

        insertInfo()

        //User properties
        firebaseAnalytics.setUserProperty("Height", heightSettings?.text.toString())
        firebaseAnalytics.setUserProperty("Weight", weightSettings?.text.toString())
        firebaseAnalytics.setUserProperty("Age", ageSettings.text.toString())
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