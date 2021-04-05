package fr.varko.mate

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.varko.mate.SettingsActivity.Companion.stringToList
import kotlinx.android.synthetic.main.plateform_row.view.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_newmessage.text = user.username
        val plateform = stringToList(user.plateform)
        var plateformList = arrayListOf<String>()
        plateform.forEach {
            val usedPlateform =  FirebaseDatabase.getInstance().getReference(("/plateform/$it"))
            usedPlateform.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    var snap = snapshot.getValue(Plateform::class.java) ?: return
                    plateformList.add(snap.name)
                    viewHolder.itemView.plateform_newmessage.text = plateformList.toString().replace("[","").replace("]", "")
                }
            })
        }
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.immageview_newmessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}