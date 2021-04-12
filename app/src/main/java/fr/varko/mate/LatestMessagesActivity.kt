package fr.varko.mate

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.collection.LLRBNode
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import fr.varko.mate.InternetCheck.Companion.isOnline
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.first_top_menu.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.profile_custom_dialog.*
import kotlinx.android.synthetic.main.settings.*

class LatestMessagesActivity : AppCompatActivity() {
    companion object{
        var currentUser: User? = null
        val adapter = GroupAdapter<ViewHolder>()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latest_message.adapter = adapter
        recyclerview_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        isOnline(this)

        ////  onClickListener des boutons du menu bas
        button_games.setOnClickListener{
            val intent = Intent(this,MyGamesActivity::class.java)
            startActivity(intent)
        }
        button_newmessage.setOnClickListener {
            val intent = Intent(this,NewMessageActivity::class.java)
            //val intent = Intent(this,GamesActivity::class.java)
            startActivity(intent)
        }
        ////
        //// onClickListener des boutons du menu haut
        button_settings.setOnClickListener {
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        button_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        ////

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        adapter.setOnItemLongClickListener { item, view ->
            val uid:String
            val row = item as LatestMessageRow
            if (row.chatMessage.fromId == FirebaseAuth.getInstance().uid) uid = row.chatMessage.toId else uid = row.chatMessage.fromId
            ProfileDialog(uid).show(supportFragmentManager, "MyCustomFragment")
            return@setOnItemLongClickListener(true)
        }

        listenForLatestMessages()
        fetchCurrentuser()
        verifyUserIsLoggedIn()
    }

    private fun fetchCurrentuser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }
        })
    }

    val latestMessageMap = HashMap<String, ChatMessage>()
    val messageList = ArrayList<ChatMessage>()

    private fun refreshRecyclerView(){
        adapter.clear()
        messageList.clear()
        latestMessageMap.values.forEach{
            messageList.add(it)
        }
        val sortedList :List<ChatMessage> = messageList.sortedByDescending { chatMessage :ChatMessage -> chatMessage.timestamp }
        sortedList.forEach{
            adapter.add((LatestMessageRow(it)))
        }

    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerView()
             }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerView()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null ){
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
             R.id.menu_new_message -> {
                 val intent = Intent(this, NewMessageActivity::class.java)
                 startActivity(intent)
             }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu , menu)
        return super.onCreateOptionsMenu(menu)
    }
}
