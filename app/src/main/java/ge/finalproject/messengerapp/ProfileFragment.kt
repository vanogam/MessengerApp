package ge.finalproject.messengerapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ProfileFragment : Fragment() {
    private lateinit var profilePicture: ImageView
    private lateinit var btnUpdate: Button
    private lateinit var btnSignOut: Button
    private lateinit var etJob: EditText
    private lateinit var tvNickname: TextView
    private lateinit var imageUri: Uri
    private val user: FirebaseUser = Firebase.auth.currentUser!!
    private lateinit var userReference: DatabaseReference
    lateinit var myView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myView = view
        initView(view)

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
            context?.let { it1 ->
                LoginActivity.signOutAndStart(requireActivity().applicationContext,
                    it1
                )
            }
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val filename = System.currentTimeMillis()
        val storageRef = FirebaseStorage.getInstance().getReference("$filename")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        Toast.makeText(context, "download url is $it", Toast.LENGTH_SHORT).show()
                        Glide.with(this).load(it).circleCrop().into(profilePicture)
                        profilePicture.setImageURI(it)
                        userReference.child("profilePicture").setValue("$filename")
                        if (progressDialog.isShowing) progressDialog.dismiss()
                    }
            }.addOnFailureListener{
                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
    }

    private fun selectImage() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        val intent = Intent()
//        intent.type = "images/*"
//        intent.action = ACTION_GET_CONTENT
        startActivityForResult(intent, ProfileActivity.PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == ProfileActivity.PICK_IMAGE) {
            imageUri = data?.data!!
            Toast.makeText(context, "Image URI is: $imageUri", Toast.LENGTH_SHORT).show()
            uploadImage()
        }
    }

    private fun initView(view: View){
        profilePicture = view.findViewById(R.id.profile_picture)
        btnUpdate = view.findViewById(R.id.update_btn)
        btnSignOut = view.findViewById(R.id.sign_out_btn)
        etJob = view.findViewById(R.id.job_et)
        tvNickname = view.findViewById(R.id.nickname_tv)
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
                                Toast.makeText(context, "uri is $it", Toast.LENGTH_SHORT).show()
                                Glide.with(this@ProfileFragment).load(it).circleCrop().into(profilePicture)
                                profilePicture.setImageURI(it)
                            }
                    }else{
                        Glide.with(this@ProfileFragment).load(R.drawable.avatar_image_placeholder).circleCrop().into(profilePicture)
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