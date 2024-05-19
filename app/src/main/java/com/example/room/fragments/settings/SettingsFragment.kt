package com.example.room.fragments.settings

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.room.R
import java.util.Calendar

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
    private lateinit var datePicker: DatePicker
    private lateinit var weightPicker: NumberPicker
    private lateinit var heightPicker: NumberPicker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        nameEditText = view.findViewById(R.id.addStepsButton)
        datePicker = view.findViewById(R.id.datePicker)
        weightPicker = view.findViewById(R.id.weightPicker)
        heightPicker = view.findViewById(R.id.heigthPicker)

        loadSavedData()

        return view
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun saveData() {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, nameEditText.text.toString())

        val calendar = Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val selectedDateInMillis = calendar.timeInMillis
        editor.putLong(KEY_BIRTHDAY, selectedDateInMillis)

        editor.putInt(KEY_WEIGHT, weightPicker.value)
        editor.putInt(KEY_HEIGHT, heightPicker.value)
        editor.apply()
    }

    private fun loadSavedData() {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        nameEditText.setText(sharedPreferences.getString(KEY_NAME, ""))

        // Recupera il timestamp salvato dalle SharedPreferences
        val savedBirthday = sharedPreferences.getLong(KEY_BIRTHDAY, 0)

        // Converte il timestamp in una data leggibile
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = savedBirthday
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Imposta la data sul DatePicker
        datePicker.updateDate(year, month, dayOfMonth)

        weightPicker.value = sharedPreferences.getInt(KEY_WEIGHT, 0)
        heightPicker.value = sharedPreferences.getInt(KEY_HEIGHT, 0)
    }
}