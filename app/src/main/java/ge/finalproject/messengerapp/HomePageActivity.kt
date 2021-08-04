package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

    }

    companion object{
        private const val USER_NICKNAME = "USER_NICKNAME"
        private const val USER_PASSWORD = "USER_PASSWORD"

        fun start(context: Context, nickname: String, password: String){
            context.startActivity(Intent(context, HomePageActivity::class.java).apply {
                putExtra(USER_NICKNAME, nickname)
                putExtra(USER_PASSWORD, password)
            })
        }
    }
}