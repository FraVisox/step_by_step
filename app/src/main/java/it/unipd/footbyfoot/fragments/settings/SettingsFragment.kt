package it.unipd.footbyfoot.fragments.settings

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.util.Log.println
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
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.Helpers


class SettingsFragment : Fragment() {

    // Class constants and default values
    companion object {
        const val WEIGHT = "weight"
        const val HEIGHT = "height"
        private const val AGE = "age"
        const val defaultWeight = 60
        const val defaultHeight = 180
        const val defaultAge = 30
    }

    // Class text views
    private lateinit var ageSettings: TextView
    private lateinit var weightSettings: TextView
    private lateinit var heightSettings: TextView

    //TODO da rivadere
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        ageSettings = view.findViewById(R.id.ageCount)
        weightSettings = view.findViewById(R.id.weightCount)
        heightSettings = view.findViewById(R.id.heightCount)

        //Get saved values, or default ones
        val preferences = requireActivity().getPreferences(MODE_PRIVATE)
        ageSettings.text = preferences.getInt(AGE, defaultAge).toString()
        weightSettings.text = preferences.getInt(WEIGHT, defaultWeight).toString()
        heightSettings.text = preferences.getInt(HEIGHT, defaultHeight).toString()

        //Listeners to buttons
        val addAgeButton: ImageButton = view.findViewById(R.id.addAgeButton)
        val subAgeButton: ImageButton = view.findViewById(R.id.subAgeButton)
        val addWeightButton: ImageButton = view.findViewById(R.id.addWeightButton)
        val subWeightButton: ImageButton = view.findViewById(R.id.subWeightButton)
        val addHeightButton: ImageButton = view.findViewById(R.id.addHeightButton)
        val subHeightButton: ImageButton = view.findViewById(R.id.subHeightButton)

        val crashButton: Button = view.findViewById(R.id.crashButton)

        firebaseAnalytics = Firebase.analytics

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

        crashButton.setOnClickListener {
            throw RuntimeException("Crash controllato")   // Solo per debug dei crash
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        val preferences = requireActivity().getPreferences(MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(AGE, ageSettings.text.toString().toInt())
        editor.putInt(WEIGHT, weightSettings.text.toString().toInt())
        editor.putInt(HEIGHT, heightSettings.text.toString().toInt())
        editor.apply()

        //examples of users properties
        firebaseAnalytics.setUserProperty("Height", heightSettings.text.toString())
        Log.w("user" ,heightSettings.text.toString())
        firebaseAnalytics.setUserProperty("Weight", weightSettings.text.toString())
        Log.w("user" ,weightSettings.text.toString())
        firebaseAnalytics.setUserProperty("Age", ageSettings.text.toString())
        Log.w("user" ,ageSettings.text.toString())
    }
}