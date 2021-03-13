package fr.varko.mate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.first_top_menu.*
import kotlinx.android.synthetic.main.settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {
    companion object{
        val uid = FirebaseAuth.getInstance().uid ?: ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ////  onClickListener des boutons du menu bas
        button_games.setOnClickListener{
            val intent = Intent(this,GamesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        button_lastmessage.setOnClickListener {
            val intent = Intent(this,LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        button_newmessage.setOnClickListener {
            val intent = Intent(this,NewMessageActivity::class.java)
            startActivity(intent)
        }
        ////
        //// onClickListener des boutons du menu haut

        button_menu.setOnClickListener {
            //val intent = Intent(this,NewMessageActivity::class.java)
            //startActivity(intent)
        }
        ////

        button_save.setOnClickListener {

            save()
        }
        button_disconnect.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        profileImageUrl.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        fetchCurrentuser()
    }

    private fun fetchCurrentuser(){
        val user = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("/users/${user.uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)
                edittextusername.setText(currentUser?.username)
                edittextdescription.setText(currentUser?.description)
                val uri = currentUser?.profileImageUrl
                val targetImageView = profileImageUrl
                Picasso.get().load(uri).into(targetImageView )
            }
        })
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profileImageUrl.setImageBitmap(bitmap)
        }
    }
    private fun save(){
        val username = FirebaseDatabase.getInstance().getReference(("/users/$uid/username"))
        username.setValue(edittextusername.text.toString())
                .addOnSuccessListener {
                    Log.d("SettingsActivity", "We save successfully username to Firebase Database")
                }
                .addOnFailureListener {
                    Log.d("SettingsActivity", "Failed to save username : ${it.message}")
                }
        val description = FirebaseDatabase.getInstance().getReference(("/users/$uid/description"))
        description.setValue(edittextdescription.text.toString())
                .addOnSuccessListener {
                    Log.d("SettingsActivity", "We save successfully description to Firebase Database")
                }
                .addOnFailureListener {
                    Log.d("SettingsActivity", "Failed to save username : ${it.message}")
                }
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        Log.d("SettingsActivity", "Successfully uploaded image :  ${it.metadata?.path}")
                        ref.downloadUrl.addOnSuccessListener {
                            Log.d("SettingsActivity", "File location : $it")
                            savePhotoToFirebaseDatabase(it.toString())
                        }
                    }
        }
        Toast.makeText(this,getString(R.string.modifsuccess), Toast.LENGTH_SHORT).show()
    }
    private fun savePhotoToFirebaseDatabase(profileImageUrl: String){
        val photo = FirebaseDatabase.getInstance().getReference(("/users/$uid/profileImageUrl"))
        photo.setValue(profileImageUrl)
                .addOnSuccessListener {
                    Log.d("SettingsActivity", "We save successfully the photo to Firebase Database")
                }
                .addOnFailureListener {
                    Log.d("SettingsActivity", "Failed to save photo : ${it.message}")
                }

    }
}
