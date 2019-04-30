package iwh.com.simplewen.win0.ybconsole.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import iwh.com.simplewen.win0.ybconsole.R
import kotlinx.android.synthetic.main.activity_test_light_app.*

/**
 * 测试站外链接，站内应用如果使用易班API会报错
 */
class TestLightApp : AppCompatActivity() {
    private lateinit var testWebView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_light_app)
        supportActionBar!!.title = intent.getStringExtra("testName")?:"测试"
        val testUrl = intent.getStringExtra("testUrl")?:""
      testWebView =  TestLightAppWebView.apply {
            this.settings.javaScriptEnabled = true
            this.loadUrl(testUrl)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && testWebView.canGoBack()) {
            testWebView.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序


    }
}
