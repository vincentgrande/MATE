package fr.varko.mate

import android.util.Log
import androidx.recyclerview.selection.SelectionTracker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import fr.varko.mate.GamesActivity.Companion.gameItems
import fr.varko.mate.SettingsActivity.Companion.uid
import kotlinx.android.synthetic.main.game_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class GameItem(val game: Game?): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.textView_game.text = game?.name.toString()
        val targetImageView = viewHolder.itemView.imageView_game
        if (game!!.img.isEmpty()) {
            targetImageView.setImageResource(R.drawable.ic_launcher_background)
        } else{
            Picasso.get().load(game!!.img).into(targetImageView);
        }

        viewHolder.itemView.checkBoxGames.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                gameItems.add(game.id)
            }else{
                gameItems.remove(game.id)
            }
        }

        val userGames = FirebaseDatabase.getInstance().getReference(("/users/$uid/playedGames"))
        userGames.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val games = SettingsActivity.stringToList(snapshot.value.toString())
                Log.d("test",games.toString())
                if(games.toString() == "" || games.toString() == "[]") return
                else {
                    games.forEach {
                        if(it == game.id.toString()) viewHolder.itemView.checkBoxGames.isChecked = true
                    }
                }
            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.game_row
    }
}