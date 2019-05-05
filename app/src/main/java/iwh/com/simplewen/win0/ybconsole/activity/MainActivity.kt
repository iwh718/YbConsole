package iwh.com.simplewen.win0.ybconsole.activity

import Utils.Tos
import android.app.AlertDialog
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
            R.id.indexMenuAbout -> {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("关于此应用")
                    .setMessage("1.应用为易班轻应用开发者使用，其它暂不支持。\n\n 2.反馈交流群：778399961（搜索添加）\n\n 3.如果你看到这个页面，应该是开发者了，自行探索吧。\n\n Author：IWH | 2019.05.5")
                    .create().show()
            }
            R.id.indexMenuNotify -> startActivity(Intent(this@MainActivity,MsgBox::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
