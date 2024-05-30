package it.unipd.footbyfoot.fragments.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.unipd.footbyfoot.R


class SettingsFragment : Fragment() {
    // Class constants
    companion object {
        const val WEIGHT = "weight"
        const val HEIGHT = "height"
        private const val AGE = "age"
        const val defaultWeight = 60
        const val defaultHeight = 180
        const val defaultAge = 30
    }

    // Class variables
    private lateinit var ageSettings: TextView
    private lateinit var weightSettings: TextView
    private lateinit var heightSettings: TextView

    private lateinit var addAgeButton: ImageButton
    private lateinit var subAgeButton: ImageButton
    private lateinit var addWeightButton: ImageButton
    private lateinit var subWeigthButton: ImageButton
    private lateinit var addHeightButton: ImageButton
    private lateinit var subHeightButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        ageSettings = view.findViewById(R.id.ageCount)
        weightSettings = view.findViewById(R.id.weightCount)
        heightSettings = view.findViewById(R.id.heightCount)

        addAgeButton = view.findViewById(R.id.addAgeButton)
        subAgeButton = view.findViewById(R.id.subAgeButton)
        addWeightButton = view.findViewById(R.id.addWeightButton)
        subWeigthButton = view.findViewById(R.id.subWeightButton)
        addHeightButton = view.findViewById(R.id.addHeightButton)
        subHeightButton = view.findViewById(R.id.subHeightButton)


        // Carica i valori precedenti dai Preferences
        val preferences = requireActivity().getPreferences(MODE_PRIVATE)
        ageSettings.text = preferences.getInt(AGE, defaultAge).toString()
        weightSettings.text = preferences.getInt(WEIGHT, defaultWeight).toString()
        heightSettings.text = preferences.getInt(HEIGHT, defaultHeight).toString()

        /*
        if (savedInstanceState != null) {
            ageSettings.text = savedInstanceState.getInt(AGE, 0).toString()
            weightSettings.text = savedInstanceState.getInt(WEIGHT, 0).toString()
            heightSettings.text = savedInstanceState.getInt(HEIGHT, 0).toString()
        }

         */

        addAgeButton.setOnClickListener {

            incrementValue(ageSettings)
        }

        subAgeButton.setOnClickListener {

            decrementValue(ageSettings)

        }

        addWeightButton.setOnClickListener {

            incrementValue(weightSettings)

        }

        subWeigthButton.setOnClickListener {

            decrementValue(weightSettings)

        }

        addHeightButton.setOnClickListener {

            incrementValue(heightSettings)

        }

        subHeightButton.setOnClickListener {

            decrementValue(heightSettings)
        }




        return view
    }

    override fun onPause() {
        super.onPause()
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(AGE, ageSettings.text.toString().toInt())
        editor.putInt(WEIGHT, weightSettings.text.toString().toInt())
        editor.putInt(HEIGHT, heightSettings.text.toString().toInt())
        editor.apply()
    }

    /*
    override fun onSaveInstanceState(savedInstanceState: Bundle)
    {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(AGE, ageSettings.text.toString().toInt())
        savedInstanceState.putInt(WEIGHT, weightSettings.text.toString().toInt())
        savedInstanceState.putInt(HEIGHT, heightSettings.text.toString().toInt())
    }

     */

    private fun incrementValue(textView: TextView) {
        var value = textView.text.toString().toInt()
        value++
        textView.text = value.toString()
    }

    private fun decrementValue(textView: TextView) {
        var value = textView.text.toString().toInt()
        if (value > 0) {
            value--
            textView.text = value.toString()
        }
    }


}
/*
class SettingsFragment : Fragment() {

    // Shared Preferences constants
    companion object {
        private const val PREFS_NAME = "UserSettings"
        private const val KEY_NAME = "name"
        private const val KEY_BIRTHDAY = "birthday"
        private const val KEY_WEIGHT = "weight"
        private const val KEY_HEIGHT = "height"
    }

    // Views
    private lateinit var nameEditText: EditText
    private lateinit var date: EditText
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        nameEditText = view.findViewById(R.id.addStepsButton)

        weightEditText = view.findViewById(R.id.weightEditText)
        heightEditText = view.findViewById(R.id.heightEditText)

        date = view.findViewById(R.id.dateText)
        loadSavedData()

        // todo  noi di fatto modifichiamo solo i dettagli dell'unico utente che abbiamo non si puo aggiungere utente ora direi
        // todo modificare il database se modifica roba
        return view
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun saveData() {
        val sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(KEY_NAME, nameEditText.text.toString())


        editor.putString(KEY_BIRTHDAY, date.text.toString())

        editor.putString(KEY_WEIGHT,  weightEditText.text.toString())
        editor.putString(KEY_HEIGHT,  heightEditText.text.toString())
        editor.apply()
    }


    private fun loadSavedData() {
        val preferences = requireActivity().getPreferences(MODE_PRIVATE)

        nameEditText.setText(preferences.getString(KEY_NAME, ""))
        date.setText(preferences.getString(KEY_BIRTHDAY, ""))
        weightEditText.setText(preferences.getString(KEY_WEIGHT, ""))
        heightEditText.setText(preferences.getString(KEY_HEIGHT, ""))
    }




}
*/
