package it.unipd.footbyfoot.fragments.workouts

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDateTime

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object {
        const val defaultHour = "null"
    }

    var hourOfDay: String = defaultHour

    var hour: Int? = null
    var minute: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default value for the picker, if not already saved.
        if (hour != null && minute != null) {
            return TimePickerDialog(activity, this, hour!!, minute!!, DateFormat.is24HourFormat(activity))
        }
        val c = LocalDateTime.now()
        val hour = c.hour
        val minute = c.minute

        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        //Sets the value and the text of the button in the activity
        this.hourOfDay = Helpers.formatTimeToString(requireContext(), hourOfDay, minute)

        this.hour = hourOfDay
        this.minute = minute

        (activity as AddWorkoutActivity).timeOfDay.text = this.hourOfDay
    }
}