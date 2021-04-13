package fr.varko.mate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.first_top_menu.*
import kotlinx.android.synthetic.main.plateform_row.*
import kotlinx.android.synthetic.main.plateform_row.view.*
import kotlinx.android.synthetic.main.settings.*
import java.util.*
import fr.varko.mate.InternetCheck.Companion.isOnline


class SettingsActivity : AppCompatActivity() {
    companion object{
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val plateformList = arrayListOf<Int>()
        val refUsers = FirebaseDatabase.getInstance().getReference(("/users/$uid"))
        val adapter = GroupAdapter<ViewHolder>()
        val plateformNameMap = HashMap<String, Plateform>()
        fun stringToList(string: String): List<String>{
            var string = string.replace("[", "")
            string = string.replace("]", "")
            string = string.replace(" ","")
            return string.split(",").toList()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        recyclerview_plateform.adapter = adapter

        isOnline(this)
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

        button_logout.setOnClickListener {
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
        edittextdescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                save()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                save()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                save()
            }
        })
        edittextusername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                save()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                save()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                save()
            }
        })
        listenForPlateform()
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
            savePhotoToFirebaseDatabase()
        }
    }

     fun save(){
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
    }
    private fun savePhotoToFirebaseDatabase(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("SettingsActivity", "Successfully uploaded image :  ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("SettingsActivity", "File location : $it")
                    val photo = FirebaseDatabase.getInstance().getReference(("/users/$uid/profileImageUrl"))
                    photo.setValue(it.toString())
                        .addOnSuccessListener {
                            Log.d("SettingsActivity", "We save successfully the photo to Firebase Database")
                        }
                        .addOnFailureListener {
                            Log.d("SettingsActivity", "Failed to save photo : ${it.message}")
                        }
                }
            }

    }

    private fun refreshRecyclerView(){
        adapter.clear()
        plateformList.clear()
        plateformNameMap.values.forEach{
            adapter.add((SettingsPlateformRow(it)))
        }
    }

    private fun listenForPlateform(){

        val ref = FirebaseDatabase.getInstance().getReference("/plateform/")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val plateformName = snapshot.getValue(Plateform::class.java) ?: return
                plateformNameMap[snapshot.key!!] = plateformName
                refreshRecyclerView()
            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val plateformName = snapshot.getValue(Plateform::class.java) ?: return
                plateformNameMap[snapshot.key!!] = plateformName
                refreshRecyclerView()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
