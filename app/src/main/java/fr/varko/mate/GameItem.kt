package fr.varko.mate

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.game_row.view.*

class GameItem(val game: Game?): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_game.text = game?.name.toString()
        Picasso.get().load(game?.image.toString()).into(viewHolder.itemView.imageView_game)
    }
    override fun getLayout(): Int {
        return R.layout.game_row
    }
}