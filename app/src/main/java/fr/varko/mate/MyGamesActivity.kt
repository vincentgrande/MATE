package fr.varko.mate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import fr.varko.mate.SettingsActivity.Companion.stringToList
import kotlinx.android.synthetic.main.activity_games.*
import kotlinx.android.synthetic.main.activity_my_games.*
import kotlinx.android.synthetic.main.my_games_row.*
import kotlinx.android.synthetic.main.plateform_row.view.*
import kotlinx.android.synthetic.main.game_row.*

class MyGamesActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    val gameItems = arrayListOf<Long>()
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    val refUsers = FirebaseDatabase.getInstance().getReference(("/users/$uid"))
    val myGamesList = arrayListOf<Int>()
    companion object{
        fun stringToList(string: String): List<String>{
            var string = string.replace("[", "")
            string = string.replace("]", "")
            string = string.replace(" ","")
            return string.split(",").toList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_games)
        recyclerview_mygames.adapter = adapter

        showMyGames()

    }
    val adapter = GroupAdapter<ViewHolder>()

    private fun showMyGames(){
        val refMyGames = refUsers.child("playedGames")
        Log.d("MyGamesActivity", "refMyGames : $refMyGames")
        refMyGames.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var snap = snapshot.getValue(String::class.java) ?: return
                Log.d("MyGamesActivity-snap", "$snap")
                val gamesId = SettingsActivity.stringToList(snap ?: "")
                Log.d("MyGamesActivity-id", "$gamesId")
                var i = 0
                gamesId.forEach {
                    var id = gamesId[i]
                    val refGames = FirebaseDatabase.getInstance().getReference("/games/$id")
                    Log.d("MyGamesActivity", "refGames : $refGames")

                    refGames.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(gameSnapshot: DataSnapshot) {
                            if(gameSnapshot.child("id").value.toString() == "null") return
                            else{
                            val gameId = id.toLong()
                            val gameName = gameSnapshot.child("name").getValue().toString()
                            val gameImg = gameSnapshot.child("img").getValue().toString()
                            val gameIsRankable = gameSnapshot.child("isRankable").getValue().toString()
                            val myGame = Game(gameId, gameName, gameImg, gameIsRankable, false)
                            adapter.add(MyGamesItem(myGame))
                            adapter.setOnItemClickListener { myGame, view ->
                                val myGameItem = myGame as MyGamesItem
                                var id:Long = myGameItem.game?.id ?: 0

                            }}
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                    Log.d("MyGamesActivity", "$refGames")
                    i++

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}