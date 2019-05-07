package iwh.com.simplewen.win0.ybconsole.activity

import Utils.Tos
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.webkit.CookieManager
import iwh.com.simplewen.win0.ybconsole.R
import okhttp3.Cookie

import work.RequestSingle

@ExperimentalCoroutinesApi
class Login : BaseActivity() {
    private lateinit var accountText:String
    private lateinit var passwordText:String
    private  val cookieList = ArrayList<Cookie>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sf1 = getSharedPreferences("loginInfo", Context.MODE_PRIVATE)

        if(sf1.getString("flag","0") !== "0"){
            login_progress.visibility = View.VISIBLE
            sign_btn.visibility = View.GONE
            accountText = sf1.getString("account","")!!
            passwordText = sf1.getString("password","")!!
            account.visibility = View.GONE
            password.visibility = View.GONE
            this.initLogin()
        }
        sign_btn.setOnClickListener {
            accountText = account.text.toString()
            passwordText = password.text.toString()
            sign_btn.visibility = View.GONE
            login_progress.visibility = View.VISIBLE
            this.initLogin()

        }




    }
    //初始化登录
  private  fun initLogin(){
        indexWebView.apply outer@{
            this.settings.apply inner@{
                this@inner.javaScriptEnabled = true
                setSupportZoom(true)
            }
            loadUrl("https://o.yiban.cn/")
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    //注入js实现登录
                    val js = "javascript:$('a.login').trigger('click');$('.dialog-content #login_name').val('18712505420');$('.dialog-content #login_pwd').val('ldd630020826');$('.userlogin').trigger('click');"
                    indexWebView.loadUrl(js)
                    //获取Cookie
                    val cookieManager = CookieManager.getInstance()
                 //   Log.d("@@token111:",cookieManager.getCookie(url).toString())
                    val token =  cookieManager.getCookie(url).split(";")
                  //  Log.d("@@token:",token.toString())
                    try {
                        if (token[1].matches(Regex(".*?yiban_user_token.*?"))) {
                            Tos("登录成功！", this@Login)
                            //保存账号与密码到私有目录
                            val sf = getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
                            sf.edit().putString("account",accountText).putString("password",passwordText)
                                .putString("flag","1").apply()

                            //取出Cookie
                            for (i in token) {
                                val cookieSin = i.split(Regex("="))
                                val cb = Cookie.Builder().name(cookieSin[0].trim()).domain("o.yiban.cn").path("/")
                                    .secure().value(cookieSin[1]).build()
                                cookieList.add(cb)

                            }
                            //同步到okHttp
                            RequestSingle.cookieStore.put("o.yiban.cn", cookieList)
                            login_progress.visibility = View.GONE
                            startActivity(Intent(this@Login,MainActivity::class.java))
                            finish()


                        } else {
                            Tos("登录失败！", this@Login)
                            account.visibility = View.VISIBLE
                            password.visibility = View.VISIBLE
                            login_progress.visibility = View.GONE
                            sign_btn.visibility = View.VISIBLE
                        }
                    }catch (e:Exception){
                        Tos("登录失败", this@Login)
                        account.visibility = View.VISIBLE
                        password.visibility = View.VISIBLE
                        login_progress.visibility = View.GONE
                        sign_btn.visibility = View.VISIBLE
                    }


                }
            }
        }

    }


}
