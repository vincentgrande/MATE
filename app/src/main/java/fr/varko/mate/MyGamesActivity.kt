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
import kotlinx.android.synthetic.main.plateform_row.view.*

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
        button_done_myGames.setOnClickListener(){
            refUsers.child("playedGames").setValue("$gameItems")
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        showMyGames()

    }
    val adapter = GroupAdapter<ViewHolder>()

    private fun showMyGames(){
        val refMyGames = refUsers.child("playedGames")

        Log.d("MyGamesActivity", "refMyGames : $refMyGames")
        refMyGames.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var snap = snapshot.getValue(String::class.java) ?: return
                val gamesId = SettingsActivity.stringToList(snap ?: "")
                Log.d("MyGamesActivity", "$gamesId")
                var i = 0
                gamesId.forEach{
                    val id = gamesId[i]
                    val refGames = FirebaseDatabase.getInstance().getReference("/games/$id")
                    Log.d("MyGamesActivity", "$refGames")
                    i++

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}