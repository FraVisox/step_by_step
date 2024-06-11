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

class PermissionDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as a dialog
        val view = inflater.inflate(R.layout.dialog_permission, container, false)

        view.findViewById<Button>(R.id.no_consent_button).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.give_consent_button).setOnClickListener {
            //ONLY WHEN THE USER GIVES CONSENT

            //Enable collection of analytics
            (activity as MainActivity).firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            //Enable collection of performances
            FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
            //Enable crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

            requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE).edit().putBoolean(MainActivity.firstUse, false).apply()
            dialog?.cancel()
        }
        return view
    }
}