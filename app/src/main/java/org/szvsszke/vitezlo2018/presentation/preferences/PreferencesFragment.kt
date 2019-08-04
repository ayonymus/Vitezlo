package org.szvsszke.vitezlo2018.presentation.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.szvsszke.vitezlo2018.R

class PreferencesFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
