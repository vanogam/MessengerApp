package ge.finalproject.messengerapp

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ge.finalproject.messengerapp.models.ChatHeader
import ge.finalproject.messengerapp.models.UserHeader
import ge.finalproject.messengerapp.presenter.ChatPresenter
import ge.finalproject.messengerapp.view.ChatListAdapter
import ge.finalproject.messengerapp.view.IChatListView
import ge.finalproject.messengerapp.view.ViewPagerAdapter

class HomePageActivity : AppCompatActivity() {
    lateinit var btnGotoProfile: ImageButton
    lateinit var btnHomePage: ImageButton
    lateinit var fab: FloatingActionButton
    lateinit var viewPager: ViewPager2
    lateinit var appBarLayout: AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        initView()
        var fragmentsList = arrayListOf(HomePageFragment(), ProfileFragment())

        var adapter = ViewPagerAdapter(this, fragmentsList)
        viewPager.adapter = adapter

        btnHomePage.setOnClickListener {
            appBarLayout.visibility = View.VISIBLE
            viewPager.currentItem = 0
        }

        btnGotoProfile.setOnClickListener {
            appBarLayout.visibility = View.GONE
            val mainLayout: ConstraintLayout = findViewById(R.id.mainLayout)
            val param: CoordinatorLayout.LayoutParams = mainLayout.layoutParams as CoordinatorLayout.LayoutParams
            param.behavior = null
            viewPager.currentItem = 1
        }

        fab.setOnClickListener{
            UserSearchActivity.start(this)
        }
    }

    companion object{
        fun startFromAuthorization(context: Context){
            context.startActivity(Intent(context, HomePageActivity::class.java))
        }
    }

    private fun initView(){
        btnGotoProfile = findViewById(R.id.goto_profile_btn)
        btnHomePage = findViewById(R.id.home_page_btn)
        fab = findViewById(R.id.fab)
        viewPager = findViewById(R.id.viewPager)
        appBarLayout = findViewById(R.id.appBarLayout)
    }

    override fun onBackPressed() {
        var intent = Intent(ACTION_MAIN)
        intent.addCategory(CATEGORY_HOME)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}