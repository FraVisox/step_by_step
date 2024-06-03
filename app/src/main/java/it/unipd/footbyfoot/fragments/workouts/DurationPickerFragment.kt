package it.unipd.footbyfoot.fragments.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.Helpers

class DurationPickerFragment : DialogFragment() {

    companion object {
        const val defaultDuration = -1L
    }

    var duration: Long = defaultDuration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as a dialog
        val view = inflater.inflate(R.layout.dialog_duration_picker, container, false)

        val hoursPicker = view.findViewById<NumberPicker>(R.id.hours)
        hoursPicker.maxValue = 23
        val minutesPicker = view.findViewById<NumberPicker>(R.id.minutes)
        minutesPicker.maxValue = 59
        val secondsPicker = view.findViewById<NumberPicker>(R.id.seconds)
        secondsPicker.maxValue = 59

        view.findViewById<Button>(R.id.ok_button).setOnClickListener {
            //Sets duration and text of activity
            duration = Helpers.getSeconds(hoursPicker.value, minutesPicker.value, secondsPicker.value)
            (activity as AddWorkoutActivity).time.text = Helpers.formatDurationToString(requireContext(), hoursPicker.value, minutesPicker.value, secondsPicker.value)
            dismiss()
        }

        return view
    }
}