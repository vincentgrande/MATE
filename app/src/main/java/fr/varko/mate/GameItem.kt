package fr.varko.mate

import androidx.recyclerview.selection.SelectionTracker
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.game_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class GameItem(val game: Game?): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.textView_game.text = game?.name.toString()
        val targetImageView = viewHolder.itemView.imageView_game
        if (game!!.img.isEmpty()) {
            targetImageView.setImageResource(R.drawable.ic_launcher_background)
        } else{
            Picasso.get().load(game!!.img).into(targetImageView);
        }
    }

    override fun getLayout(): Int {
        return R.layout.game_row
    }
}