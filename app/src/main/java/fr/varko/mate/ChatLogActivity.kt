package fr.varko.mate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
    }
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username
        //setUpData()
        listenForMessages()
        send_button.setOnClickListener{
            Log.d(TAG,"Attempt to send message ...")

            performSendMessage()
        }
    }
    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY).uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-messages/$fromId/$toId")
        val refTo = FirebaseDatabase.getInstance().getReference("/users-messages/$toId/$fromId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null){
                    Log.d(TAG,"MESSAGE : ${chatMessage?.text}")

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                    } else {
                        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

                        adapter.add(ChatToItem(chatMessage.text,toUser))
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })

    }


    private fun performSendMessage(){
        var text = enter_message.text.toString()
        if (text == "") return
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid
        if (fromId == null) return
        val reference = FirebaseDatabase.getInstance().getReference("/users-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/users-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                enter_message.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)
        val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageFromRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }
}


