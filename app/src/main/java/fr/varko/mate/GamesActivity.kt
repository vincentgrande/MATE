package fr.varko.mate


import kotlinx.android.synthetic.main.activity_games.*
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
import kotlinx.android.synthetic.main.game_row.*

class GamesActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_games)
        recyclerview_games.adapter = adapter
        database = FirebaseDatabase.getInstance()
        button_done_games.setOnClickListener(){
            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            //startActivity(intent)
        }
        ShowGame()
    }
    val adapter = GroupAdapter<ViewHolder>()

    private fun ShowGame(){
        val gameItems: MutableList<GameItem> = mutableListOf()
        val ref = FirebaseDatabase.getInstance().getReference("/games")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("GamesActivity",it.toString())
                    val game = it.getValue(Game::class.java)
                    adapter.add(GameItem(game))
                    adapter.setOnItemClickListener { game, view ->
                        val gameItem = game as GameItem
                        if (gameItems.contains(gameItem)){
                            gameItems.remove(gameItem)
                        }else{
                            gameItems.add(gameItem)
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