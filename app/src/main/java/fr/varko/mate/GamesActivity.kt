package fr.varko.mate


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import fr.varko.mate.SettingsActivity.Companion.stringToList
import kotlinx.android.synthetic.main.activity_games.*
import kotlinx.android.synthetic.main.game_row.*
import kotlinx.android.synthetic.main.game_row.view.*


class GamesActivity : AppCompatActivity() {
    companion object{
        var gameItems = mutableSetOf<Long>()
    }
    private lateinit var database: FirebaseDatabase

    private val uid = FirebaseAuth.getInstance().uid ?: ""
    val refUsers = FirebaseDatabase.getInstance().getReference(("/users/$uid"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)
        recyclerview_games.adapter = adapter
        database = FirebaseDatabase.getInstance()
        button_done_games.setOnClickListener(){
            Log.d("GamesActivity", "ICI : $gameItems")

            refUsers.child("playedGames").setValue("$gameItems")
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        fetchGames()
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
                            view.checkBoxGames.isChecked = false
                        } else {
                            //imageView_game.setBackgroundResource(R.drawable.border)
                            gameItems.add(id)
                            view.checkBoxGames.isChecked = true

                            Log.d("GamesActivity", "LA $id")

                        }

                        Log.d("GamesActivity", "$gameItem")
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun fetchGames(){
        val userGames = FirebaseDatabase.getInstance().getReference(("/users/$uid/playedGames"))
        userGames.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val games = stringToList(snapshot.value.toString())
                Log.d("test",games.toString())
                if(games.toString() == "" || games.toString() == "[]") return
                else {
                    games.forEach {
                        gameItems.add(it.toLong())
                    }
                }
            }
        })
        }


    }