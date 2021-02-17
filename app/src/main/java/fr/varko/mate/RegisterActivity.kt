package fr.varko.mate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title="MATE Register"
        register.setOnClickListener {
            register()
        }
        already.setOnClickListener {
            Log.d("MainActivity","Try to show login activity")
            //Launch the new activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            select.text = ""
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            select_photo_imageView.setImageBitmap(bitmap)
            select_photo.alpha = 0f
        }
    }
    private fun register() {
        val username: String = username_input.text.toString()
        val email: String = email_input.text.toString()
        val password: String = password_input.text.toString()
        Log.d("MainActivity","Username : $username")
        Log.d("MainActivity","Email : $email")
        Log.d("MainActivity","Password : $password")
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter email/password",Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this,"Success !",Toast.LENGTH_SHORT).show()
                    Log.d("Main","Success with uid ${it.result?.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                    Log.d("Main","Failed to create user : ${it.message}")
                }
    }
    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image :  ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                     Log.d("RegisterActivity","File location : $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference(("/users/$uid"))
        Log.d("RegisterActivity","REF : $ref")
        val user = User(uid,username_input.text.toString(),profileImageUrl)
        Log.d("RegisterActivity","USER : ${user.username} ${user.profileImageUrl}")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "We save successfully the user to Firebase Database")
                val intent = Intent(this,GamesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to save user : ${it.message}")
                }
    }
}
