package org.szvsszke.vitezlo2018.presentation.listitems

import androidx.annotation.StringRes
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