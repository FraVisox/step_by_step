package it.unipd.footbyfoot.fragments.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.fragments.Helpers

class DurationPickerFragment : DialogFragment() {

    companion object {
        const val defaultDuration = -1L

        //Total duration
        var duration: Long = defaultDuration

        //Saved instance keys
        const val hoursKey = "hours"
        const val minutesKey = "minutes"
        const val secondsKey = "seconds"
    }

    //Seconds, minutes and hours
    var seconds: Int = defaultDuration.toInt()
    var minutes: Int = defaultDuration.toInt()
    var hours: Int = defaultDuration.toInt()

    //Pickers
    private var hoursPicker: NumberPicker? = null
    private var minutesPicker: NumberPicker? = null
    private var secondsPicker: NumberPicker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as a dialog
        val view = inflater.inflate(R.layout.dialog_duration_picker, container, false)

        //Initialize the pickers
        hoursPicker = view.findViewById(R.id.hours)
        hoursPicker?.maxValue = 23
        minutesPicker = view.findViewById(R.id.minutes)
        minutesPicker?.maxValue = 59
        secondsPicker = view.findViewById(R.id.seconds)
        secondsPicker?.maxValue = 59

        //If there is an instance saved
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt(secondsKey)
            minutes = savedInstanceState.getInt(minutesKey)
            hours = savedInstanceState.getInt(hoursKey)
        }

        //If there is a saved instance or the duration has already been set
        if (seconds != defaultDuration.toInt() && minutes != defaultDuration.toInt() && hours != defaultDuration.toInt()) {
            secondsPicker?.value = seconds
            minutesPicker?.value = minutes
            hoursPicker?.value = hours
        }

        view.findViewById<Button>(R.id.ok_button).setOnClickListener {
            //Return if the duration is zero
            val dur = Helpers.getSecondsFromTime(hoursPicker!!.value, minutesPicker!!.value, secondsPicker!!.value)
            if (dur == 0L) {
                Toast.makeText(context, getString(R.string.impossible_duration), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Sets duration and text of activity
            seconds = secondsPicker!!.value
            minutes = minutesPicker!!.value
            hours = hoursPicker!!.value
            duration = dur
            (activity as AddWorkoutActivity).time.text = Helpers.formatDurationToString(requireContext(), hours, minutes, seconds)
            dismiss()
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //Save current selected time
        super.onSaveInstanceState(outState)
        if (secondsPicker != null && minutesPicker != null && hoursPicker != null) {
            outState.putInt(secondsKey, secondsPicker!!.value)
            outState.putInt(minutesKey, minutesPicker!!.value)
            outState.putInt(hoursKey, hoursPicker!!.value)
        }
    }
}