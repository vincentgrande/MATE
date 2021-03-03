package fr.varko.mate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_my_games.*

class MyGamesActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    val refUsers = FirebaseDatabase.getInstance().getReference(("/users/$uid"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_games)
        recyclerview_mygames.adapter = adapter
        showMyGames()

    }
    val adapter = GroupAdapter<ViewHolder>()

    private fun showMyGames(){
        val refMyGames = refUsers.child("playedGames")
        refMyGames.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("MyGamesActivity", it.toString())
                    val game = it.getValue(Game::class.java)
                    adapter.add(GameItem(game))
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}