package org.szvsszke.vitezlo2018.presentation.listitems

import androidx.annotation.StringRes
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.info_box_item.*
import org.szvsszke.vitezlo2018.R

class InfoBoxItem(@StringRes private val label: Int,
                  private val content: String
): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textViewInfoLabel.setText(label)
            textViewInfoContent.text = content
        }
    }

    override fun getLayout() = R.layout.info_box_item
}