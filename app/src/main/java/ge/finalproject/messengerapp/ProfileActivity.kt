package ge.finalproject.messengerapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_PICK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var profilePicture: ImageView
    private lateinit var btnUpdate: Button
    private lateinit var btnSignOut: Button
    private lateinit var etJob: EditText
    private lateinit var tvNickname: TextView
    private lateinit var imageUri: Uri
    private val user: FirebaseUser = Firebase.auth.currentUser!!
    private lateinit var userReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initView()

        userReference = Firebase.database.reference
                        .child("users")
                        .child(user.uid)

        setField("nickname")
        setField("job")
        setField("profilePicture")

        updateInfo()

        profilePicture.setOnClickListener{
            selectImage()
        }

        btnSignOut.setOnClickListener{
            Firebase.auth.signOut()
            LoginActivity.signOutAndStart(applicationContext, this)
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val filename = System.currentTimeMillis()
        val storageRef = FirebaseStorage.getInstance().getReference("$filename")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        Toast.makeText(this, "download url is $it", Toast.LENGTH_SHORT).show()
                        Glide.with(this).load(it).circleCrop().into(profilePicture)
                        profilePicture.setImageURI(it)
                        userReference.child("profilePicture").setValue("$filename")
                        if (progressDialog.isShowing) progressDialog.dismiss()
                    }
            }.addOnFailureListener{
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
    }

    private fun selectImage() {
        var intent = Intent(ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        val intent = Intent()
//        intent.type = "images/*"
//        intent.action = ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data!!
            Toast.makeText(this, "Image URI is: $imageUri", Toast.LENGTH_SHORT).show()
            uploadImage()
        }
    }

    private fun initView(){
        profilePicture = findViewById(R.id.profile_picture)
        btnUpdate = findViewById(R.id.update_btn)
        btnSignOut = findViewById(R.id.sign_out_btn)
        etJob = findViewById(R.id.job_et)
        tvNickname = findViewById(R.id.nickname_tv)
    }

    private fun setField(field: String){
        val fieldReference = userReference.child(field)

        val fieldListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (field == "nickname") {
                    tvNickname.text = dataSnapshot.value.toString()
                }
                else if (field == "job"){
                    etJob.setText(dataSnapshot.value.toString())
                }
                else{
                    var filename = dataSnapshot.value.toString()
                    if (filename != ""){
                        val storageRef = FirebaseStorage.getInstance().getReference(filename)
                        storageRef.downloadUrl
                            .addOnSuccessListener {
                                Toast.makeText(this@ProfileActivity, "uri is $it", Toast.LENGTH_SHORT).show()
                                Glide.with(this@ProfileActivity).load(it).circleCrop().into(profilePicture)
                                profilePicture.setImageURI(it)
                            }
                    }else{
                        Glide.with(this@ProfileActivity).load(R.drawable.avatar_image_placeholder).circleCrop().into(profilePicture)
                        profilePicture.setImageDrawable(resources.getDrawable(R.drawable.avatar_image_placeholder))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "error in ProfileActivity: ${error.message}")
            }
        }

        fieldReference.addValueEventListener(fieldListener)
    }


    private fun updateInfo(){
        btnUpdate.setOnClickListener{
            userReference.child("job").setValue(etJob.text.toString())
        }
    }

    companion object{
        const val PICK_IMAGE = 100

        fun start(context: Context){
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }

}