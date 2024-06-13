package it.unipd.footbyfoot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance

//Initial dialog presented to the user, asks permissions for telemetry usage
class PermissionDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_permission, container, false)

        //If no consent is given, simply make the user use the application
        view.findViewById<Button>(R.id.no_consent_button).setOnClickListener {
            dismiss()
        }

        //ONLY WHEN THE USER GIVES CONSENT enable Firebase
        view.findViewById<Button>(R.id.give_consent_button).setOnClickListener {
            //Enable collection of analytics
            (activity as MainActivity).firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            //Enable collection of performances
            FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
            //Enable crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

            //Save the user choice
            requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE).edit().putBoolean(MainActivity.firstUse, false).apply()
            dialog?.cancel()
        }
        return view
    }
}