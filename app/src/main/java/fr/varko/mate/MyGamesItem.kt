package fr.varko.mate

import androidx.recyclerview.selection.SelectionTracker
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.my_games_row.view.*


class MyGamesItem(val game: Game?): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.gameName_textView.text = game?.name.toString()
        val targetImageView = viewHolder.itemView.imageViewMyGame
        if (game!!.img.isEmpty()) {
            targetImageView.setImageResource(R.drawable.ic_launcher_background)
        } else{
            Picasso.get().load(game!!.img).into(targetImageView);
        }
    }

    override fun getLayout(): Int {
        return R.layout.my_games_row
    }
}