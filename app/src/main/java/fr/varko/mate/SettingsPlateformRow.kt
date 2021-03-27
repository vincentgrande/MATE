package fr.varko.mate


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.Settings.Global.getString
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.plateform_row.view.*

class SettingsPlateformRow (val plateform: Plateform): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.checkBox_plateform.setText(plateform?.name.toString())

    }

    override fun getLayout(): Int {
        return R.layout.plateform_row
    }
}