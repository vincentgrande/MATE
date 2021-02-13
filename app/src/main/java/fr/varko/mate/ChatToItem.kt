package fr.varko.mate

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, val user: User ): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_to
        Picasso.get().load(uri).into(targetImageView )
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}