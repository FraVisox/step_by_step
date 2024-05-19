package com.example.room.fragments.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.room.MainActivity
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
        heightEditText = view.findViewById(R.id.heigthEditText)

        datePicker = view.findViewById(R.id.datePicker)
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

        val data = Calendar.getInstance()
        data.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        editor.putLong(KEY_BIRTHDAY, data.timeInMillis)

        editor.putString(KEY_WEIGHT,  weightEditText.text.toString())
        editor.putString(KEY_HEIGHT,  heightEditText.text.toString())
        editor.apply()
    }


    private fun loadSavedData() {
        val preferences = requireActivity().getPreferences(MODE_PRIVATE)

        nameEditText.setText(preferences.getString(KEY_NAME, ""))
        DatePickerUpdate(preferences.getLong(KEY_BIRTHDAY, 0))
        weightEditText.setText(preferences.getString(KEY_WEIGHT, ""))
        heightEditText.setText(preferences.getString(KEY_HEIGHT, ""))
    }


    private fun DatePickerUpdate(dateBirthDay :Long){

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateBirthDay
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Imposta la data sul DatePicker
        datePicker.updateDate(year, month, dayOfMonth)
    }


}