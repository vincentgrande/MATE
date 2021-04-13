package fr.varko.mate

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.profile_custom_dialog.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class ProfileDialog(val uid:String): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);
        return inflater.inflate(R.layout.profile_custom_dialog, container, false)
    }
    override fun onStart() {
        textview_games.text = "${getString(R.string.games)} : "
        textview_plateform.text = "${getString(R.string.plateform)} : "
        val partner = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var chatPartnerUser:User
        partner.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)!!
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(imageview_profile)
                textview_username.text = chatPartnerUser?.username
                if(chatPartnerUser?.description.equals(""))
                else edittext_description.setText(chatPartnerUser?.description)
                if (chatPartnerUser.playedGames.equals("[]") || chatPartnerUser.playedGames.equals(""))
                else {
                    val games = SettingsActivity.stringToList(chatPartnerUser.playedGames)
                    var gamesList = arrayListOf<String>()
                    games.forEach {
                        val UsedGames = FirebaseDatabase.getInstance().getReference(("/games/$it"))
                        UsedGames.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var snap = snapshot.getValue(Game::class.java) ?: return
                                gamesList.add(snap.name)
                                textview_games.text = "${getString(R.string.games)} : ${gamesList.toString().replace("[", "").replace("]", "")}"
                            }
                        })
                    }
                }
                if (chatPartnerUser.plateform.equals("[]") || chatPartnerUser.plateform.equals(""))
                else {
                    val plateform = SettingsActivity.stringToList(chatPartnerUser.plateform)
                    var plateformList = arrayListOf<String>()
                    plateform.forEach {
                        val usedPlateform = FirebaseDatabase.getInstance().getReference(("/plateform/$it"))
                        usedPlateform.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var snap = snapshot.getValue(Plateform::class.java) ?: return
                                if (snap != null) {
                                    plateformList.add(snap.name)
                                    textview_plateform.text = "${getString(R.string.plateform)} : ${plateformList.toString().replace("[", "").replace("]", "")}"
                                }
                            }
                        })
                    }
                }
            }
        })
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}