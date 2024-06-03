package it.unipd.footbyfoot.fragments.workouts

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.Helpers
import java.time.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var year: Int? = null
    var dayOfYear: Int? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker, or the one saved
        var c = LocalDate.now()

        if (year != null && dayOfYear != null && dayOfYear != 0) {
            c = LocalDate.ofYearDay(year!!, dayOfYear!!)
        }

        val year = c.year
        val month = c.month.value-1
        val day = c.dayOfMonth

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        //Sets the values and the text of the button in the activity
        val date = LocalDate.of(year, month+1, day)
        if (date > LocalDate.now()) {
            Toast.makeText(requireContext(), R.string.impossible_date, Toast.LENGTH_SHORT).show()
            return
        }

        this.year = year
        this.dayOfYear = date.dayOfYear

        (activity as AddWorkoutActivity).date.text = Helpers.formatDateToString(requireContext(), date)
    }
}