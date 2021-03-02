package fr.varko.mate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.first_top_menu.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ////  onClickListener des boutons du menu bas
        button_games.setOnClickListener{
            val intent = Intent(this,GamesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        button_lastmessage.setOnClickListener {
            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        button_newmessage.setOnClickListener {
            val intent = Intent(this,NewMessageActivity::class.java)
            startActivity(intent)
        }
        ////
        //// onClickListener des boutons du menu haut

        button_menu.setOnClickListener {
            //val intent = Intent(this,NewMessageActivity::class.java)
            //startActivity(intent)
        }
        ////
        fetchCurrentuser()
    }

    private fun fetchCurrentuser(){
        val user = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("/users/${user.uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)
                edittextusername.setText(currentUser?.username)
                val uri = currentUser?.profileImageUrl
                val targetImageView = profileImageUrl
                Picasso.get().load(uri).into(targetImageView )
            }
        })
    }
}
