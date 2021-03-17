package fr.varko.mate


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_games.*


class GamesActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    val gameItems = arrayListOf<Long>()
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    val refUsers = FirebaseDatabase.getInstance().getReference(("/users/$uid"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)
        recyclerview_games.adapter = adapter
        database = FirebaseDatabase.getInstance()
        button_done_games.setOnClickListener(){
            refUsers.child("playedGames").setValue("$gameItems")
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        ShowGame()
    }
    val adapter = GroupAdapter<ViewHolder>()

    private fun ShowGame(){
        val refGames = FirebaseDatabase.getInstance().getReference("/games")
        refGames.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("GamesActivity", it.toString())
                    val game = it.getValue(Game::class.java)
                    adapter.add(GameItem(game))
                    adapter.setOnItemClickListener { game, view ->
                        val gameItem = game as GameItem
                        var id:Long = gameItem.game?.id ?: 0
                        if (gameItems.contains(id)) {
                            gameItems.remove(id)
                        } else {
                            gameItems.add(id)
                        }
                        Log.d("GamesActivity", "$gameItems")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}