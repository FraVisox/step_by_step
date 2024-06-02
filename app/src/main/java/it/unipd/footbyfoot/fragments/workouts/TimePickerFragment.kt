package it.unipd.footbyfoot.fragments.workouts

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import it.unipd.footbyfoot.fragments.summary.Helpers
import java.time.LocalDateTime
import java.time.LocalTime

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var hourOfDay = "00:00" //TODO: stringa

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = LocalDateTime.now()
        val hour = c.hour
        val minute = c.minute

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        //Sets the value and the text of the button in the activity
        this.hourOfDay = Helpers.formatTimeToString(requireContext(), hourOfDay, minute)

        (activity as AddWorkoutActivity).timeOfDay.text = this.hourOfDay
    }
}