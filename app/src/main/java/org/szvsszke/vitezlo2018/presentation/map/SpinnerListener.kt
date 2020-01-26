package org.szvsszke.vitezlo2018.presentation.map

import android.view.View
import android.widget.AdapterView

/**
 * Kotlin convenience wrapper
 */
class SpinnerListener(private val action: (position: Int) -> Unit) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                position: Int, id: Long) {
        action.invoke(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit
}