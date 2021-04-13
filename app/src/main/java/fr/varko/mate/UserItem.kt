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
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.plateform_row.view.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_newmessage.text = user.username
        if(user.plateform != "[]" && user.plateform != "") {
            val plateform = stringToList(user.plateform)
            var plateformList = arrayListOf<String>()
            plateform.forEach {
                val usedPlateform = FirebaseDatabase.getInstance().getReference(("/plateform/$it"))
                usedPlateform.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var snap = snapshot.getValue(Plateform::class.java) ?: return
                        plateformList.add(snap.name)
                        viewHolder.itemView.plateform_newmessage.text = plateformList.toString().replace("[", "").replace("]", "")
                    }
                })
            }
        }
        if(user.playedGames != "[]" && user.playedGames != "[]") {
            val games = stringToList(user.playedGames)
            var gamesList = arrayListOf<String>()
            games.forEach {
                val usedGames = FirebaseDatabase.getInstance().getReference(("/games/$it"))
                Log.d("UserItem", "$usedGames")
                usedGames.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshotGame: DataSnapshot) {
                        val gameName = snapshotGame.child("name").getValue().toString()
                        gamesList.add(gameName)
                        viewHolder.itemView.textView_games.text = gamesList.toString().replace("[", "").replace("]", "")
                    }
                })
            }
        }
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.immageview_newmessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}