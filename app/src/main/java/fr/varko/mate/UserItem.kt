package fr.varko.mate

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_newmessage.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.immageview_newmessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}