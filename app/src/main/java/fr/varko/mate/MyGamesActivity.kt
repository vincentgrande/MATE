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
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.first_top_menu.*

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
        manage_games.setText(R.string.managegames)
        showMyGames()
        manage_games.setOnClickListener {
            val intent = Intent(this,GamesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        ////  onClickListener des boutons du menu bas
        button_lastmessage.setOnClickListener{
            val intent = Intent(this,LatestMessagesActivity::class.java)
            startActivity(intent)
        }
        button_newmessage.setOnClickListener {
            val intent = Intent(this,NewMessageActivity::class.java)
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