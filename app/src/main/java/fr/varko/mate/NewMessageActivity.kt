package fr.varko.mate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        fetchUsers()
    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage",it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null){
                        if (user.uid != FirebaseAuth.getInstance().uid){
                            Log.d("NewMessage",FirebaseAuth.getInstance().uid)
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
}



