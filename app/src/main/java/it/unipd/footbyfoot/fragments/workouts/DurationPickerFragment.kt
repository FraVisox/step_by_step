package it.unipd.footbyfoot.fragments.workouts

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DurationPickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var hourOfDay = "00:00" //TODO: dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance() //TODO: metti ora con localdate
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time the user picks.
        this.hourOfDay = "$hourOfDay:$minute"

        (activity as AddWorkoutActivity).timeOfDay.text = this.hourOfDay
    }
}