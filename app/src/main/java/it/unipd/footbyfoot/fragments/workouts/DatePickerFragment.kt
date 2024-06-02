package it.unipd.footbyfoot.fragments.workouts

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var year: Int? = null
    var dayOfYear: Int? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = LocalDate.now()
        val year = c.year
        val month = c.month.value
        val day = c.dayOfMonth

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        //Sets the values and the text of the button in the activity
        val date = LocalDate.of(year, month, day)
        this.year = year
        this.dayOfYear = date.dayOfYear

        (activity as AddWorkoutActivity).date.text = Helpers.formatDateToString(requireContext(), date)
    }
}