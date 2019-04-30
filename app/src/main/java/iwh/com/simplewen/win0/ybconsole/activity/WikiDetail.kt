package iwh.com.simplewen.win0.ybconsole.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import iwh.com.simplewen.win0.ybconsole.R
import kotlinx.android.synthetic.main.activity_wiki_detail.*

class WikiDetail : AppCompatActivity() {
    private lateinit var cuDetailWebView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiki_detail)

        supportActionBar?.title = intent.getStringExtra("apiName") ?: "易班API接口"
        cuDetailWebView = detailWebView
        cuDetailWebView.loadUrl(intent.getStringExtra("apiUrl"))
        cuDetailWebView.apply {
            this.webChromeClient = WebChromeClient()
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            this.settings.loadWithOverviewMode = true
            this.settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val dong = "javascript:$('.mainwrapper').css('width','100%');$('#mw-panel').hide();$('#content').css({'margin':'0','width':'100%','padding':'0','min-height':'100%'});$('table').css('width','100%');" +
                            "$('.header').hide();$('.content_hd').hide();$('.bodyContent').css({'margin':'0','padding':'0'});"
                    val dong2 = "javascript:$('.inner').css('min-height','');$('.copyright').hide();"
                    cuDetailWebView.loadUrl(dong)
                    cuDetailWebView.loadUrl(dong2)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && cuDetailWebView.canGoBack()) {
            cuDetailWebView.goBack()//返回上个页面
            return true
        }
        return super.onKeyDown(keyCode, event)//退出整个应用程序


    }
}
