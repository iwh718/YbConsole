package iwh.com.simplewen.win0.ybconsole.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    }
}
