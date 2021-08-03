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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoginActivity.start(this)
    }

}