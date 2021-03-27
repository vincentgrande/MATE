package fr.varko.mate


import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.varko.mate.SettingsActivity.Companion.plateformList
import kotlinx.android.synthetic.main.plateform_row.view.*

class SettingsPlateformRow (val plateform: Plateform): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.checkBox_plateform.setText(plateform?.name.toString())
        viewHolder.itemView.checkBox_plateform.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) plateformList.add(plateform?.id)
            else if(!isChecked) plateformList.remove(plateform?.id)
            Log.d("ici", plateformList.toString())
        }
        val usedPlateform =  FirebaseDatabase.getInstance().getReference(("/users/${SettingsActivity.uid}/plateform/"))
        usedPlateform.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                var snap = snapshot.getValue(String::class.java) ?: return
                snap = snap.replace("[", "")
                snap = snap.replace("]", "")
                snap = snap.replace(" ","")
                val plateformId = snap.split(",").toList()
                if(plateformId.contains(plateform.id.toString())){
                    viewHolder.itemView.checkBox_plateform.setChecked(true)
                }
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.plateform_row
    }
}