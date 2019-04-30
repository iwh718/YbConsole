package iwh.com.simplewen.win0.ybconsole.activity

import Utils.Tos
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import iwh.com.simplewen.win0.ybconsole.R
import kotlinx.android.synthetic.main.activity_app_info.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import work.RequestSingle
@ExperimentalCoroutinesApi
class AppInfo : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)
        supportActionBar?.title = intent.getStringExtra("appName")
        RequestSingle.getAppInfo(intent.getStringExtra("appId"),this@AppInfo)
        val appLogoUrl = intent.getStringExtra("appLogoUrl")
        Glide.with(this@AppInfo).load(appLogoUrl).apply(RequestOptions.bitmapTransform(CircleCrop())).into(appInfoLogo)
        appInfoName.text = intent.getStringExtra("appName")?:"暂无获取"
        AppInfoOutSiteAdd.setOnClickListener {
            startActivity(Intent(this@AppInfo,TestLightApp::class.java).apply {
                putExtra("testUrl",RequestSingle.AppInfoData[0].appOutSideAdd)
                putExtra("testName",intent.getStringExtra("appName"))
                startActivity(this)
            })
        }
        AppInfoSiteAdd.setOnClickListener {
            AlertDialog.Builder(this@AppInfo).setTitle("注意！")
                .setMessage("站内应用请使用易班查看！")
                .setPositiveButton("继续"){
                    _,_ ->

                      val pkgName = packageManager.getLaunchIntentForPackage("com.yiban.app")
                    if (pkgName != null){
                        startActivity(pkgName)
                    }else{
                        Tos("未安装易班！",this@AppInfo)
                    }

                }
                .setNegativeButton("取消",null)
                .create().show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.app_info_menu,menu)
      return  true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.lightMoreChart -> Tos("查看图表。。",this@AppInfo)
        }
        return super.onOptionsItemSelected(item)
    }
}
