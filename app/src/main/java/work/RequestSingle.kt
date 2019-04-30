package work


import Utils.Tos
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.modal.LightItem
import iwh.com.simplewen.win0.ybconsole.activity.modal.UserInfo
import iwh.com.simplewen.win0.ybconsole.activity.modal.WikiMobileApi
import iwh.com.simplewen.win0.ybconsole.activity.modal.WikiWebApi
import kotlinx.android.synthetic.main.light_app_fragment.*
import kotlinx.android.synthetic.main.user_fragment.*
import kotlinx.android.synthetic.main.wiki_fragment.*
import kotlinx.coroutines.*
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * 单例网络请求
 * @author IWH
 * desc：易班开发者第三方android客户端
 */
@ExperimentalCoroutinesApi
object RequestSingle {
    var cookieStore: HashMap<String, MutableList<Cookie>> = HashMap()
    private val client = OkHttpClient.Builder().cookieJar(object : CookieJar {
        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            return this@RequestSingle.cookieStore[url.host()] ?: Collections.emptyList()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            this@RequestSingle.cookieStore[url.host()] = cookies
        }
    }).connectTimeout(8, TimeUnit.SECONDS).build() //初始化请求

    private const val manageUrl = "https://o.yiban.cn/manage/index"
    private const val userUrl = "https://o.yiban.cn/global/user"
    private const val wikiUrl = "https://o.yiban.cn/wiki/index.php?page=%E6%98%93%E7%8F%ADapi"
    private const val lightBaseUrl = "https://o.yiban.cn/manage/appinfo?appid="//应用详情地址
    val LightDataNoAuth = ArrayList<LightItem>()//未认证
    val LightDataAuth = ArrayList<LightItem>()//认证应用
    val wikiWebApiData = ArrayList<WikiWebApi>()//web接口
    val wikiMobileData = ArrayList<WikiMobileApi>()//移动接口
    val UserInfoData = ArrayList<UserInfo>()//开发者信息

    /**
     * 获取轻应用信息
     */
    fun getManage(coroutines: BaseActivity, sort: Int = 2) = coroutines.launch Manage@{
        this@RequestSingle.LightDataAuth.clear()
        this@RequestSingle.LightDataNoAuth.clear()
        val nowUrl = when (sort) {
            0 -> this@RequestSingle.manageUrl
            else -> "${this@RequestSingle.manageUrl}?item=2"
        }
        this@RequestSingle.client.newCall(Request.Builder().url("https://o.yiban.cn/manage/index?item=3").build())
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val resText = response.body()?.string()
                    val doc = Jsoup.parse(resText)
                    val itemNoAuth = doc.select(".choose_3")

                    val authItems = doc.select(".choose_3 .has-auth tbody tr")//通过认证
                    val noAuthItems = itemNoAuth.select(".no-auth tbody tr")//未通过
                    //   Log.d("@@noAuth:",authItems.toString())
                    try {
                        for (i in noAuthItems) {
                            val lightAppData = LightItem(
                                i.select(".table-control")[0].select(".description").html(),//应用名称
                                i.select("td")[0].select(".app-icon").attr("src"),//logo链接
                                i.select("td")[1].html(),//权限等级
                                i.select("td")[2].html(),//应用状态
                                i.select("td")[0].select(".description").attr("href").replace(
                                    "/manage/appinfo?appid=",
                                    ""
                                )//应用Id
                            )
                            this@RequestSingle.LightDataNoAuth.add(lightAppData)
                        }
                        //  Log.d("@@noAuthData:",this@RequestSingle.LightDataNoAuth.toString())

                    } catch (e: Exception) {
                        coroutines.launch(Dispatchers.Main) {
                            Tos("数据出现问题！未通过审核页面", coroutines)
                        }
                    }
                    //获取通过审核应用
                    try {
                        if (authItems.size <= 0) {

                        } else {
                            for (i in authItems) {
                                val lightAppData = LightItem(
                                    i.select(".table-control")[0].select(".description").html(),//应用名称
                                    i.select("td")[0].select(".app-icon").attr("src"),//logo链接
                                    i.select("td")[1].html(),//权限等级
                                    i.select("td")[2].select(".switch-box .txt-true").html(),//应用状态
                                    i.select("td")[0].select(".description").attr("href").replace(
                                        "/manage/appinfo?appid=",
                                        ""
                                    )//应用Id
                                )
                                this@RequestSingle.LightDataAuth.add(lightAppData)
                            }
                            Log.d("@@authData:", this@RequestSingle.LightDataAuth.toString())
                        }

                    } catch (e: Exception) {
                        coroutines.launch(Dispatchers.Main) {
                            Tos("数据出现问题:通过审核页面！", coroutines)
                        }
                    }

                    //   Log.d("@@lightDataOver！:", "--------")
                    this@RequestSingle.LightDataNoAuth.addAll(this@RequestSingle.LightDataAuth)
                    coroutines.launch(Dispatchers.Main) {
                        coroutines.lightRefresh.isRefreshing = false
                        coroutines.lightRecycle.adapter!!.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    coroutines.launch(Dispatchers.Main) { Tos("网络连接失败！", coroutines) }
                }
            })

    }

    /**
     * 获取开发者信息
     */
    fun getUserInfo(coroutines: BaseActivity) = coroutines.launch {
        this@RequestSingle.client.newCall(Request.Builder().url(this@RequestSingle.userUrl).build())
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val result = response.body()!!.string()
                    val doc = Jsoup.parse(result)
                    try {
                        val userInfo = UserInfo(
                            coderAlias = doc.select(".nav-user span").html(),
                            coderLogo = doc.select(".nav-user img").attr("src"),
                            coderEmail = doc.select("#change_email").`val`(),
                            coderNumber = doc.select("#change_phone").`val`(),
                            coderId = doc.select(".user-info .form-group")[6].select(".yw-input").attr("value"),
                            coderName = doc.select(".user-info .form-group")[4].select(".yw-input").attr("value"),
                            coderSchool = doc.select(".user-info .form-group")[5].select(".yw-input").attr("value"),
                            coderType = doc.select(".user-info .form-group")[7].select(".yw-input").attr("value")

                        )
                        coroutines.launch(Dispatchers.Main) {
                            with(coroutines) {
                                coderAlias.text = "开发者：${userInfo.coderAlias}"
                                coderEmail.text = "邮箱：${userInfo.coderEmail}"
                                coderId.text = "学号：${userInfo.coderId}"
                                Glide.with(this).load(userInfo.coderLogo).apply(
                                    RequestOptions.bitmapTransform(CircleCrop())
                                ).into(this.coderLogo)
                                coderNumber.text = "电话：${userInfo.coderNumber}"
                                coderSchool.text = "学校：${userInfo.coderSchool}"
                                coderType.text = "身份：${userInfo.coderType}"
                                coderName.text = "姓名：${userInfo.coderName}"

                            }
                            // Log.d("@@userInfo:","获取信息完成！")
                            // Log.d("@@userInfo", userInfo.toString())
                        }


                    } catch (e: Exception) {
                        Log.w("@@error:", e.stackTrace.toString())
                    }

                }

                override fun onFailure(call: Call, e: IOException) {
                    coroutines.launch(Dispatchers.Main) { Tos("网络连接错误！", coroutines) }
                }
            })
    }

    /**
     * 获取API文档
     */
    fun getWiki(coroutines: BaseActivity) = coroutines.launch {
        this@RequestSingle.client.newCall(Request.Builder().url(this@RequestSingle.wikiUrl).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    coroutines.launch(Dispatchers.Main) {
                        Tos("网络连接失败！", coroutines)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    //临时存放TdName
                    var tmpTdName = ""
                    val res = response.body()?.string()
                    val doc = Jsoup.parse(res)
                    val tables = doc.select("#bodyContent table")
                    val mobileData = tables.last()
                    tables.removeAt(0)
                    tables.removeAt(0)
                    tables.removeAt(tables.size-1)//去除移动端列表
                    Log.d("@@mobile:",mobileData.html())
                    try {
                        //开始匹配web接口
                        for (i in tables.indices) {
                            val wTr = tables[i].select("tbody tr")
                            for( n in wTr){
                                var wName:String
                                var wDesc:String
                                var wUrl:String
                                var wInterface:String
                                val ntd = n.select("td")
                                //跨列td
                                if(ntd.size == 2){
                                    wInterface = ntd[0].select("a").html()
                                    wUrl = "https://o.yiban.cn${ntd[0].select("a").attr("href")}"
                                    wDesc = ntd[1].html()
                                    wName = tmpTdName
                                }else{
                                    tmpTdName = ntd[0].html()
                                    wName = ntd[0].html()
                                    wInterface = ntd[1].select("a").html()
                                    wUrl = "https://o.yiban.cn${ntd[1].select("a").attr("href")}"
                                    wDesc = ntd[2].html()
                                }

                                val wikiWebData = WikiWebApi(
                                    wikiUrl = wUrl,
                                    wikiDescribe =  wDesc,
                                    wikiInterfaceName =  wInterface,
                                    wikiName =  wName
                                )
                                Log.d("@@wiki: ***********", wikiWebData.toString())
                                this@RequestSingle.wikiWebApiData.add(wikiWebData)
                            }

                        }
                    }catch (e:Exception){
                        coroutines.launch (Dispatchers.Main){
                            Tos("web端接口获取失败！",coroutines)
                        }
                        Log.d("@@error:",e.stackTrace.toString())
                    }finally {
                      //  Log.d("@@wkiApiSize:",this@RequestSingle.wikiWebApiData.size.toString())

                    }

                  try {
                      val mobilList = mobileData.select("tbody tr")
                      for (m in mobilList){
                          val wikiMobileData = WikiMobileApi(
                              wikiUrl = "https://o.yiban.cn${m.select("td")[0].select("a").attr("href")}",
                              wikiName = m.select("td")[0].select("a").html(),
                              wikiDescribe = m.select("td")[1].html()
                          )
                          this@RequestSingle.wikiMobileData.add(wikiMobileData)
                        //  Log.d("@@wikiMobile:",wikiMobileData.toString())
                          Log.d("@@wikiMobleSize:",this@RequestSingle.wikiMobileData.size.toString())
                      }
                  }catch (e:Exception){
                      Log.d("@@errorMobile:",e.stackTrace.toString())
                      coroutines.launch (Dispatchers.Main){
                          Tos("移动端接口获取失败！",coroutines)
                      }
                  }  //开始获取移动端API


                    coroutines.launch(Dispatchers.Main) {
                        coroutines.wikiMobileApiRecycle.adapter!!.notifyDataSetChanged()
                        coroutines.wikiWebApiRecycle.adapter!!.notifyDataSetChanged()
                    }


                }
            })
    }



    fun getAppInfo(appId:String,coroutines: BaseActivity) = coroutines.launch {
        this@RequestSingle.client.newCall(Request.Builder().url("${this@RequestSingle.lightBaseUrl}$appId").build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch {
                    Tos("网络连接错误",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()!!.string()
                val doc = Jsoup.parse(res)
                Log.d("@@AppInfo:",doc.html())

            }
        })
    }


}