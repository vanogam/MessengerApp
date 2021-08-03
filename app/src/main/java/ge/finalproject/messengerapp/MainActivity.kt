package ge.finalproject.messengerapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var etNickname: EditText
    private lateinit var etPassword: EditText
    private lateinit var etJob: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnGotoSignUp: Button
    private lateinit var btnSignUp: Button
    private lateinit var layerAuthorize: LinearLayout
    private lateinit var layerRegister: LinearLayout
    private lateinit var auth: FirebaseAuth
//    private var isAuthMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = this.getSharedPreferences(PREFERENCE_FILE, MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        auth = Firebase.auth

        btnSignIn.setOnClickListener(View.OnClickListener {
            val nickname: String = etNickname.text.toString()
            val password: String = etPassword.text.toString()

            if (nickname.isEmpty()){
                Toast.makeText(this, "Enter nickname!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password.isEmpty()){
                Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            auth.signInWithEmailAndPassword(nickname + DOMAIN_NAME, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        startActivity(Intent(this, HomePageActivity::class.java).apply {
                            putExtra("User", user)
                        })
                    } else {
                        Toast.makeText(baseContext, "Authentication failed",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        })

        btnGotoSignUp.setOnClickListener(View.OnClickListener() {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(AUTH_MODE, false)
            })
        })

        btnSignUp.setOnClickListener(View.OnClickListener() {
            auth = Firebase.auth;
            val nickname: String = etNickname.text.toString()
            val password: String = etPassword.text.toString()
            val job: String = etJob.text.toString()

            if (nickname.isEmpty()){
                Toast.makeText(this, "Enter nickname!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password.isEmpty()){
                Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (job.isEmpty()){
                Toast.makeText(this, "Enter job!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            auth.createUserWithEmailAndPassword(nickname + DOMAIN_NAME, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        // StartActivity monacemebis gadacemit intentshi
                    } else {
                        Log.d("err",  task.exception.toString());
                        Toast.makeText(baseContext, "Registration failed",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        })
    }

    private fun initView() {
        etNickname = findViewById(R.id.nickname_et)
        etPassword = findViewById(R.id.password_et)
        etJob = findViewById(R.id.job_et)
        btnSignIn = findViewById(R.id.sign_in_btn)
        btnGotoSignUp = findViewById(R.id.goto_sign_up_btn)
        btnSignUp = findViewById(R.id.sign_up_btn)
        layerAuthorize = findViewById(R.id.authorization_layout)
        layerRegister = findViewById(R.id.registration_layout)

        if (!(intent.getBooleanExtra(AUTH_MODE, true))){
            layerAuthorize.visibility = View.GONE
            layerRegister.visibility = View.VISIBLE
        }
    }

    companion object{
        const val PREFERENCE_FILE = "FILENAME"
        const val AUTH_MODE = "AUTH_MODE"
        const val DOMAIN_NAME = "@gmail.com"
    }

}