package org.szvsszke.vitezlo2018.presentation.listitems

import androidx.annotation.StringRes
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem

class SimpleExpandableItem(@StringRes titleStringResId: Int)
    : SimpleItem(titleStringResId), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}
