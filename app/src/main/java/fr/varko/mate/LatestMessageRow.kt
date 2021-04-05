package fr.varko.mate

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.last_message.text = chatMessage.text
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) chatPartnerId = chatMessage.toId else chatPartnerId = chatMessage.fromId

        val partnerUsername = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        partnerUsername.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                val uri = chatPartnerUser?.profileImageUrl
                viewHolder.itemView.username_last_message.text = chatPartnerUser?.username
                val targetImageView = viewHolder.itemView.imageview_profile
                Picasso.get().load(uri).into(targetImageView)
            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}