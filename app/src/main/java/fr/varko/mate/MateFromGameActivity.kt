package fr.varko.mate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.activity_mate_from_game.*

class MateFromGameActivity : AppCompatActivity() {
    companion object{
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mate_from_game)
        fetchUsers()
    }
    val adapter = GroupAdapter<ViewHolder>()
    private fun fetchUsers(){
        adapter.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null){
                        if (user.uid != FirebaseAuth.getInstance().uid) {
                            adapter.add(UserItem(user))
                        }
                    }
                    adapter.setOnItemClickListener { item, view ->
                        val userItem = item as UserItem
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(NewMessageActivity.USER_KEY,userItem.user)
                        startActivity(intent)
                        finish()
                    }
                }
                recyclerview_newmessage.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun fetchUsersSameGames(){
        adapter.clear()
        var mates = arrayListOf<String>()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)

                    if (user != null ){
                        if (user.uid != FirebaseAuth.getInstance().uid){
                            ////////
                            val mate_plateform =
                                SettingsActivity.stringToList(user?.plateform ?: "")
                            val currentUser_plateform = SettingsActivity.stringToList(
                                LatestMessagesActivity.currentUser?.plateform ?: ""
                            )
                            currentUser_plateform.forEach {current ->
                                mate_plateform.forEach{ mate ->
                                    if(current == mate && user.uid !in mates){
                                        mates.add(user.uid)
                                        adapter.add(UserItem(user))
                                    }
                                }
                            }
                        }
                        ////////
                    }
                    adapter.setOnItemClickListener { item, view ->
                        val userItem = item as UserItem
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(NewMessageActivity.USER_KEY,userItem.user)
                        startActivity(intent)
                        finish()
                    }
                }
                recyclerview_newmessage.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}