package iwh.com.simplewen.win0.ybconsole.activity

import Utils.Tos
import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.adapter.ViewPageAdapter
import iwh.com.simplewen.win0.ybconsole.activity.fragments.LightApp
import iwh.com.simplewen.win0.ybconsole.activity.fragments.UserManage
import iwh.com.simplewen.win0.ybconsole.activity.fragments.WikiManage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import work.RequestSingle

/**
 * 易班开发者。。是否烂尾，，，看看吧
 */

@ExperimentalCoroutinesApi
class MainActivity : BaseActivity() {

    //底部栏监听器
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_home -> {

                indexPage.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                indexPage.currentItem = 1

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                indexPage.currentItem = 2

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //设置底部栏监听器
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //设置ViewPage
        val fgList = arrayListOf(LightApp(),WikiManage(),UserManage())
        indexPage.apply {
            adapter = ViewPageAdapter(supportFragmentManager,fgList)
            offscreenPageLimit = 2
            addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) = Unit
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)  = Unit

                override fun onPageSelected(position: Int) {
                    navigation.menu.getItem(position).isChecked = true
                }
            })
        }

    }
    override fun onBackPressed() {
        with(Intent()) {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
            startActivity(this)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.index_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.indexMenuAbout -> Tos("关于。。",this@MainActivity)
            R.id.indexMenuNotify -> startActivity(Intent(this@MainActivity,MsgBox::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
