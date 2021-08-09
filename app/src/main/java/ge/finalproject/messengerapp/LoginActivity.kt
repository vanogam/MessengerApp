package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.finalproject.messengerapp.utils.GlideApp


class LoginActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_login)

        initView()

        auth = Firebase.auth

        btnSignIn.setOnClickListener(View.OnClickListener {
            val nickname: String = etNickname.text.toString()
            val password: String = etPassword.text.toString()

            if (nickname.isEmpty()){
                Toast.makeText(this, ENTER_EMAIL_MESSAGE, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password.isEmpty()){
                Toast.makeText(this, ENTER_PASS_MESSAGE, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            login(nickname, password)
        })

        btnGotoSignUp.setOnClickListener(View.OnClickListener() {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra(AUTH_MODE, false)
            })
        })

        btnSignUp.setOnClickListener(View.OnClickListener() {
            auth = Firebase.auth;
            val nickname: String = etNickname.text.toString()
            val password: String = etPassword.text.toString()
            val job: String = etJob.text.toString()

            if (nickname.isEmpty()){
                Toast.makeText(this, ENTER_EMAIL_MESSAGE, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password.isEmpty()){
                Toast.makeText(this, ENTER_PASS_MESSAGE, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (job.isEmpty()){
                Toast.makeText(this, ENTER_JOB_MESSAGE, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            auth.createUserWithEmailAndPassword(nickname, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        login(nickname, password)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, EMAIL_IN_USE_MESSAGE, Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            Toast.makeText(this, WEAK_PASS_MESSAGE, Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, BAD_FORMAT_MESSAGE, Toast.LENGTH_SHORT).show()
                        }
                        catch (e: Exception) {
                            Log.e("ERROR", "exception: $e")
                        }
                    }
                }
        })
    }

    private fun login(nickname: String, password: String) {
        auth.signInWithEmailAndPassword(nickname, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    HomePageActivity.startFromAuthorization(this)
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, INVALIDITY_MESSAGE, Toast.LENGTH_SHORT).show()
                    } catch (e: FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, INVALID_USER_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
            }
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

        const val BAD_FORMAT_MESSAGE = "The email address is badly formatted"
        const val EMAIL_IN_USE_MESSAGE = "Email already in use"
        const val WEAK_PASS_MESSAGE = "Password should be at least 6 characters"
        const val INVALID_USER_MESSAGE = "There is no user record corresponding to this email"
        const val INVALIDITY_MESSAGE = "Invalid email or password"

        const val ENTER_EMAIL_MESSAGE = "Enter email!"
        const val ENTER_PASS_MESSAGE = "Enter password!"
        const val ENTER_JOB_MESSAGE = "Enter job!"

        fun start(context: Context){
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

}