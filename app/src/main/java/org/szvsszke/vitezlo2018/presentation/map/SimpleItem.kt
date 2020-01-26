package org.szvsszke.vitezlo2018.presentation.map

import androidx.annotation.StringRes
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_simple.*
import org.szvsszke.vitezlo2018.R

open class SimpleItem(@StringRes private val text: Int): Item()  {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.textView_simpleItem.setText(text)
    }

    override fun getLayout() = R.layout.item_simple
}

class SimpleExpandableItem(@StringRes titleStringResId: Int)
    : SimpleItem(titleStringResId), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}
