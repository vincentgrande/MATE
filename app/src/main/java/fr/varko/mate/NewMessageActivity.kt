package fr.varko.mate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import fr.varko.mate.LatestMessagesActivity.Companion.currentUser
import fr.varko.mate.SettingsActivity.Companion.stringToList
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.second_top_menu.*
import kotlinx.android.synthetic.main.user_row_newmessage.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {
    companion object{
        val USER_KEY = "USER_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        InternetCheck.isOnline(this)
        toptext.text = getString(R.string.selectnewmessage)
        checkBox_sameplateform.text = getString(R.string.sameplateform)
        checkBox_sameGames.text = getString(R.string.samegames)
        back_button.setOnClickListener{
            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
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
        checkBox_sameplateform.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                fetchUsersSamePlateform()
                checkBox_sameGames.isChecked = false
            }
            if( checkBox_sameGames.isChecked == false && checkBox_sameplateform.isChecked == false){
                fetchUsers()
            }
        }
        checkBox_sameGames.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                fetchUsersSameGames()
                checkBox_sameplateform.isChecked = false
            }
             if( checkBox_sameGames.isChecked == false && checkBox_sameplateform.isChecked == false){
                 fetchUsers()
             }
        }
        adapter.setOnItemLongClickListener { item, view ->
            val uid = item as UserItem
            ProfileDialog(uid.user.uid).show(supportFragmentManager, "MyCustomFragment")
            return@setOnItemLongClickListener(true)
        }
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
                        intent.putExtra(USER_KEY,userItem.user)
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
    private fun fetchUsersSamePlateform(){
        adapter.clear()
        var mates = arrayListOf<String>()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)

                    if (user != null ){
                        if (user.uid != FirebaseAuth.getInstance().uid){
                            ////////
                            val mate_plateform = stringToList(user?.plateform?: "")
                            val currentUser_plateform = stringToList(currentUser?.plateform ?: "")
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
                        intent.putExtra(USER_KEY,userItem.user)
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
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)

                    if (user != null ){
                        if (user.uid != FirebaseAuth.getInstance().uid){
                            ////////
                            val mate_game = stringToList(user?.playedGames?: "")
                            val currentUser_game = stringToList(currentUser?.playedGames ?: "")
                            currentUser_game.forEach {current ->
                                mate_game.forEach{ mate ->
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
                        intent.putExtra(USER_KEY,userItem.user)
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



