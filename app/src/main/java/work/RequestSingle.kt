package work


import Utils.Tos
import Utils.showNetError
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.MainActivity
import iwh.com.simplewen.win0.ybconsole.activity.modal.*
import kotlinx.android.synthetic.main.activity_add_light_app.*
import kotlinx.android.synthetic.main.activity_app_info.*
import kotlinx.android.synthetic.main.activity_edit_app.*
import kotlinx.android.synthetic.main.activity_msg_box.*
import kotlinx.android.synthetic.main.debug_layout.*
import kotlinx.android.synthetic.main.debug_layout.view.*
import kotlinx.android.synthetic.main.light_app_fragment.*
import kotlinx.android.synthetic.main.user_fragment.*
import kotlinx.android.synthetic.main.wiki_fragment.*
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
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
     val client = OkHttpClient.Builder().cookieJar(object : CookieJar {
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
    private const val msgBoxUrl = "https://o.yiban.cn/global/msgbox"//消息列表
    private const val msgInfoUrl = "https://o.yiban.cn/ajax/readmsg"//消息内容
    private const val modifyUrl = "https://o.yiban.cn/ajax/modify"
    private const val debugUrl = "https://o.yiban.cn/page/goto?act=iapp_debug&appid="
    val LightDataNoAuth = ArrayList<LightItem>()//未认证
    val LightDataAuth = ArrayList<LightItem>()//认证应用
    val wikiWebApiData = ArrayList<WikiWebApi>()//web接口
    val wikiMobileData = ArrayList<WikiMobileApi>()//移动接口
    val UserInfoData = ArrayList<UserInfo>()//开发者信息
    val AppInfoData = ArrayList<AppInfo>()//轻应用详细数据
    val MsgBoxData = ArrayList<MsgBoxList>()//消息列表
    val ApiData = ArrayList<Map<String,Any>>()
    var runFlag = 0


    /**
     * 获取轻应用信息
     */
    fun getManage(coroutines: BaseActivity, sort: Int = 2) = coroutines.launch Manage@{
        this@RequestSingle.LightDataAuth.clear()
        this@RequestSingle.LightDataNoAuth.clear()
        //处理后期扩展
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
                         //   Log.d("@@authData:", this@RequestSingle.LightDataAuth.toString())
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
                   // Log.d("@@mobile:",mobileData.html())
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
                             //   Log.d("@@wiki: ***********", wikiWebData.toString())
                                this@RequestSingle.wikiWebApiData.add(wikiWebData)
                            }

                        }
                    }catch (e:Exception){
                        coroutines.launch (Dispatchers.Main){
                            Tos("web端接口获取失败！",coroutines)
                        }
                      //  Log.d("@@error:",e.stackTrace.toString())
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
                        //  Log.d("@@wikiMobleSize:",this@RequestSingle.wikiMobileData.size.toString())
                      }
                  }catch (e:Exception){
                    //  Log.d("@@errorMobile:",e.stackTrace.toString())
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



    fun getAppInfo(appId:String,coroutines: BaseActivity,corId:Int = 0) = coroutines.launch {
        this@RequestSingle.AppInfoData.clear()
        this@RequestSingle.client.newCall(Request.Builder().url("${this@RequestSingle.lightBaseUrl}$appId").build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch {
                    Tos("网络连接错误",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body()!!.string()
                val doc = Jsoup.parse(res)
                val article = doc.select(".test-user")
                val lisCount = article.select(".list-cont")
                val checkStatusEle = lisCount[0].select("ul li")[0].select(".content")
                val isOnline = checkStatusEle.select("a").isNullOrEmpty()

                try{
                    val appInfoData = AppInfo(
                        appID = article.select(".clearfix .user-info .pull-right .details")[0].html(),
                        appPassword = article.select(".clearfix .user-info .pull-right .details")[1].html(),
                        appDesc = lisCount[1].select("ul li")[1].select(".content").html(),
                        appOutSideAdd = lisCount[2].select("ul li")[2].select(".content").html(),
                        appShow =  lisCount[1].select("ul li")[3].select(".content").html(),
                        appSideAdd = lisCount[2].select("ul li")[0].select(".content").html(),
                        appStatus = if(isOnline)checkStatusEle.html() else checkStatusEle.select("a").html(),
                        appTestUrl = lisCount[3].select("ul li")[0].select(".content img").attr("src"),
                        appUse =  lisCount[1].select("ul li")[4].select(".content").html(),
                        sLogoUrl =  lisCount[1].select("ul li")[5].select("img")[1].attr("src"),
                        lLogoUr =  lisCount[1].select("ul li")[5].select("img")[0].attr("src"),
                        appName = lisCount[1].select("ul li")[0].select(".content")[0].html()

                    )
                    this@RequestSingle.AppInfoData.add(appInfoData)
                 //   Log.d("@@AppInfo:",appInfoData.toString())


                    coroutines.launch(Dispatchers.Main) {
                       when(corId){
                           0 -> {
                               this@RequestSingle.showCharts(appId,coroutines = coroutines)
                               this@RequestSingle.showCharts(appId,"apistat",coroutines)
                               with(coroutines){
                                   AppInfoStatus.text = "审核状态：${appInfoData.appStatus}"
                                   AppInfoDesc.text = "简介:${appInfoData.appDesc}"
                                   AppInfoOutSiteAdd.text ="站外地址:${ appInfoData.appOutSideAdd}"
                                   AppInfoSiteAdd.text = "站内地址:${appInfoData.appSideAdd}"
                                   AppInfoUse.text = "使用场景:${appInfoData.appUse}"
                                   AppInfoShow.text = "用户可见:${appInfoData.appShow}"
                                   AppInfoID.text = appInfoData.appID
                                   AppInfoOnlineBtn.visibility = if(isOnline){
                                       View.GONE
                                   }else {
                                       View.VISIBLE
                                   }
                                   AppInfoPassword.text = appInfoData.appPassword
                                   Glide.with(coroutines).load(appInfoData.appTestUrl).into(AppInfoTest)
                               }
                           }
                           1 ->{
                                with(coroutines){

                                  //可见度
                                    when{
                                        //开发者同校
                                        appInfoData.appShow.matches(".*?\\u4ec5\\u4e0e\\u5f00\\u53d1\\u8005.*?".toRegex()) ->{
                                        modifyShow.setSelection(2)


                                        }
                                        //校方认证
                                        appInfoData.appShow.matches(".*?\\u4ec5\\u6821\\u65b9\\u8ba4\\u8bc1.*?".toRegex())->{
                                            modifyShow.setSelection(1)

                                        }
                                        //所有用户
                                        else->{
                                            modifyShow.setSelection(0)
                                        }

                                    }
                                    //场景
                                    when{
                                        //仅易班客户端
                                        appInfoData.appUse.matches(".*?\\u4ec5\\u6613\\u73ed.*?".toRegex())->{
                                            modifyUse.setSelection(0)

                                        }
                                        //兼容模式Pc/客户端
                                        appInfoData.appUse.matches(".*?\\u517c\\u5bb9.*?".toRegex())->{
                                            modifyUse.setSelection(1)

                                        }

                                    }

                                    modifyDesc.setText(appInfoData.appDesc)
                                    modifyName.setText(appInfoData.appName)
                                    modifySideAdd.setText(appInfoData.appOutSideAdd)
                                    Glide.with(coroutines).load(appInfoData.sLogoUrl).apply(RequestOptions.bitmapTransform(CircleCrop())).into(editSLogo)
                                    Glide.with(coroutines).load(appInfoData.lLogoUr).apply(RequestOptions.bitmapTransform(CircleCrop())).into(editLLogo)
                                }
                           }

                       }
                    }
                }catch (e:Exception){
                   // Log.d("@@appInfoError:",e.stackTrace.toString())
                    coroutines.launch (Dispatchers.Main){
                        Tos("解析失败！",coroutines)
                    }
                }



            }
        })
    }

    /**
     * 获取预览
     */
    fun getDebugImg(coroutines: BaseActivity,appId:String) = coroutines.launch {
        this@RequestSingle.client.newCall(Request.Builder().url("${this@RequestSingle.debugUrl}$appId").build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch (Dispatchers.Main){
                    Tos("解析失败！",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
               val text = response.body()!!.string()
                val doc =Jsoup.parse(text)
                val imgUrl = doc.select(".qrcode img").attr("src")
                coroutines.launch (Dispatchers.Main){
                    val ly = coroutines.layoutInflater.inflate(R.layout.debug_layout ,null)
                    Glide.with(coroutines).load(imgUrl).into(ly.debugImg)
                    AlertDialog.Builder(coroutines).setView(ly).create().show()
                }
            }
        })
    }


    /**
     * 获取消息
     */
    fun getMsgBoxList(coroutines: BaseActivity){
        this@RequestSingle.MsgBoxData.clear()
        this@RequestSingle.client.newCall(Request.Builder().url(this@RequestSingle.msgBoxUrl).build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch (Dispatchers.Main){
                    Tos("解析失败！",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val text = response.body()!!.string()
               try {
                   val doc =Jsoup.parse(text)
                   val lists = doc.select(".message-list ul li")
                   for (d in lists){
                       val msgBoxData = MsgBoxList(
                           msgId = d.select(".content").attr("data-id") ,
                           msgName = d.select(".content .text").html() ,
                           msgTime =d.select("time").html()
                       )
                       //添加进列表
                     //  Log.d("@@msg:", msgBoxData.toString())
                       this@RequestSingle.MsgBoxData.add(msgBoxData)
                   }
                  // Log.d("@@进入UI协程:", "---------------")
                   coroutines.launch (Dispatchers.Main){
                      // Log.d("@@更新列表:", "---------------")
                       coroutines.msgBoxRecycle.adapter!!.notifyDataSetChanged()
                   }
               }catch (e :Exception){
                   coroutines.launch (Dispatchers.Main){
                       Tos("解析失败！",coroutines)
                   }
               }
            }
        })
    }

    /**
     *
     * 获取站内消息
     */
    fun getMsgInfo(msgId:String,coroutines: BaseActivity) = coroutines.launch {

        val formBody = FormBody.Builder().add("msg_id",msgId).build()

        this@RequestSingle.client.newCall(Request.Builder().url(this@RequestSingle.msgInfoUrl).post(formBody).build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch (Dispatchers.Main){
                    Tos("解析失败！",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val text = response.body()!!.string()
              // Log.d("**json:",text.toString())
                try {
                    val res = JSONObject(text)["msgInfo"] as JSONObject
                    val title = res["title"] as String
                    val content = res["msg"] as String
                    val cbody = Jsoup.parse(content).select(".indent").html()
                    val cauthor = Jsoup.parse(content).select(".alignRight p")[0].html()
                    val ctime = Jsoup.parse(content).select(".alignRight p")[1].html()
                  //  Log.d("** $title  $content","-------------解析完成！**")
                    coroutines.launch (Dispatchers.Main){
                        AlertDialog.Builder(coroutines).setIcon(R.drawable.ic_notifications_black_24dp)
                            .setTitle(title)
                            .setMessage("$cbody \n\n $cauthor \n\n $ctime").create().show()
                    }
                }catch (e:Exception){
                    coroutines.launch (Dispatchers.Main){
                        Tos("解析失败！",coroutines)
                    }
                }
            }
        })
    }



    /**
     * 删除应用 && 下架应用 && 提交审核
     * url：https://o.yiban.cn/ajax/delapp
     */
    fun appManage(appId:String, coroutines: BaseActivity, type:Int = 0) = coroutines.launch{
        var url = "https://o.yiban.cn/ajax/delapp"
        var tips = "删除"
        when(type){
            0 -> {
                url = "https://o.yiban.cn/ajax/delapp"
                tips = "删除"
            }
            1 -> {
                url = "https://o.yiban.cn/ajax/downapp"
                tips = "下架"
            }
            2 ->{
                url = "https://o.yiban.cn/ajax/appcheck"
                tips  = "提交审核"
            }
        }

            this@RequestSingle.client.newCall(Request.Builder().url(url).post(FormBody.Builder().add("appid",appId).add("type","audit").build()).build()).enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    coroutines.launch {
                        Tos("$tips 失败:网络错误！",coroutines)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val text = response.body()!!.string()
                   // Log.d("@@$tips :",text)
                    try {
                        if(text.matches(".*?s200.*?".toRegex())){
                            coroutines.launch (Dispatchers.Main){
                                Tos("$tips 完成！",coroutines)
                                coroutines.startActivity(Intent(coroutines,MainActivity::class.java))
                                coroutines.finish()
                            }
                        }else{
                            coroutines.launch (Dispatchers.Main){
                                Tos("$tips 失败:${JSONObject(text).get("msgCN") as String}",coroutines)
                            }
                        }
                    }catch (e:Exception){
                        coroutines.launch(Dispatchers.Main) {
                            Tos("$tips ：解析错误！",coroutines)
                        }
                    }
                }
            })
    }



    /**
     * 添加轻应用
     * url1:https://o.yiban.cn/ajax/addinfo
     * url2:https://o.yiban.cn/ajax/addline
     */
    fun addApp(bundle:Bundle,coroutines: BaseActivity) = coroutines.launch{
       // Log.d("@@AddReceive:",bundle.toString())
        val inSideAddUrl = bundle.get("mUrl") as String
        val formBody = FormBody.Builder()
            .add("app_name",bundle.getString("mName")!!)
            .add("app_intro",bundle.getString("mDesc")!!)
            .add("app_slogo",bundle.getString("msLogoUrl")!!)
            .add("app_blogo",bundle.getString("mlLogoUrl")!!)
            .add("app_type","iapp")
            .add("app_label_id","工具")
            .add("app_viewlevel",bundle.getString("mShowLevel")!!)
            .add("app_canweb","")
            .build()
        this@RequestSingle.client.newCall(Request.Builder().url("https://o.yiban.cn/ajax/addinfo").post(formBody).build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch(Dispatchers.Main){
                    Tos("添加失败：网络错误！",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
               try {
                   val res = response.body()!!.string()
                   //如果返回appId
                   if(res.length < 10){
                     //  Log.d("@@addResult,Ok:",res)
                       coroutines.launch {

                         //  Log.d("@@mUrl:","+++++++++++++ $inSideAddUrl")
                           this@RequestSingle.continueAdd(res.trim(),inSideAddUrl,coroutines)
                       }
                   }else{
                       try {
                           val resCode = JSONObject(res).get("msgCN") as String
                           coroutines.launch {
                               Tos(resCode,coroutines)
                               coroutines.upPro.visibility = View.GONE
                               coroutines.cardTop.visibility = View.VISIBLE
                               coroutines.addNextBtn.visibility = View.VISIBLE
                           }

                       }catch (e:Exception){
                          // Log.d("JSON解析失败！","-------------${e.stackTrace}")
                       }
                   }
                 //  Log.d("@@addResult:",res)
               }catch (e:Exception){
                  // Log.d("@@addError:",e.stackTrace.toString())
               }

            }
        })

    }

    /**
     * 添加第二步
     * @param mUrl 站外地址
     * url:https://o.yiban.cn/ajax/addline
     */
    private fun continueAdd(appId:String,mUrl:String,coroutines: BaseActivity) = coroutines.launch {
        val formBody = FormBody.Builder()
            .add("appid",appId)
            .add("app_url","")
            .add("app_url_rd","")
            .add("app_url_th",mUrl)
            .add("app_type","iapp")
            .build()
        this@RequestSingle.client.newCall(Request.Builder().url("https://o.yiban.cn/ajax/addline").post(formBody).build()).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                showNetError(coroutines,"添加")
            }

            override fun onResponse(call: Call, response: Response) {
                val rs = response.body()!!.string()
             //   Log.d("@@addOver:",rs)
                if(rs.trim() != "s200"){
                    try {
                        coroutines.launch (Dispatchers.Main){
                            Tos("${JSONObject(rs).get("msgCN")}",coroutines)
                            coroutines.upPro.visibility = View.GONE
                            coroutines.cardTop.visibility = View.VISIBLE
                            coroutines.addNextBtn.visibility = View.VISIBLE

                        }

                    }catch (e:Exception){

                      //  Log.d("@@addOver:Error",e.stackTrace.toString())

                    }
                }else{
                    coroutines.launch(Dispatchers.Main) {
                        Tos("创建完成！",coroutines)
                        coroutines.upPro.visibility = View.GONE
                        coroutines.addLightSubmit.show()
                    }
                }

            }
        })
    }

    /**
     * 查看数据
     * @param type 数据类型
     * @param appId AppId
     * @param coroutines 协程上下文
     * user url:https://o.yiban.cn/manage/oauthstat?appid=用户数据
     * api url = https://o.yiban.cn/manage/apistat?appid=捷库调用
     */
    fun showCharts(appId:String,type:String = "oauthstat",coroutines: BaseActivity) = coroutines.launch{

        if(this@RequestSingle.runFlag > 1){
            this@RequestSingle.ApiData.clear()
            this@RequestSingle.runFlag = 0
        }
        this@RequestSingle.client.newCall(Request.Builder().url("https://o.yiban.cn/manage/$type?appid=$appId").build()).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                showNetError(coroutines,"获取数据")
            }

            override fun onResponse(call: Call, response: Response) {
                val rs = response.body()!!.string()
                val docScript = Jsoup.parse(rs).select("body script")[1]


               try {
                   val m = docScript.html().split(";".toRegex())
                   val data = m[0].replace("var","").replace("pieData","").replace("=","")

                   if(type == "oauthstat"){
                       for (i in 0 until JSONArray(data).length()-1){
                           val arr = JSONArray(data)[i] as JSONObject
                           this@RequestSingle.ApiData.add(mapOf(arr.get("name") as String to arr.get("value")))
                           // Log.d("@@oauthJson:",arr.toString())
                       }
                   }else{
                       for (i in 0 until JSONArray(data).length()){
                           val arr = JSONArray(data)[i] as JSONObject
                           this@RequestSingle.ApiData.add(mapOf(arr.get("name") as String to arr.get("value")))
                           // Log.d("@@apiJson:",arr.toString())
                       }
                   }
                 //  Log.d("@@oauthJson:",this@RequestSingle.ApiData.toString())
                //   Log.d("@@runFlag:",this@RequestSingle.runFlag.toString())
                   if(this@RequestSingle.runFlag == 1){
                       coroutines.launch{(Dispatchers.Main)
                           coroutines.appInfoUserAdd.text = "新增：${this@RequestSingle.ApiData[0]["新增率"]}"
                           coroutines.appInfoUserLoss.text = "流失：${this@RequestSingle.ApiData[1]["流失率"]}"
                           coroutines.appInfoUserBack.text = "回头：${this@RequestSingle.ApiData[2]["回头率"]}"
                           coroutines.appInfoApiSuccess.text = "API成功：${this@RequestSingle.ApiData[3]["成功次数"]}"
                           coroutines.appInfoApiFailed.text = "API失败：${this@RequestSingle.ApiData[4]["出错次数"]}"
                       }
                   }
               }catch (e:Exception){
                //Log.d("@@error:json解析错误！","-------------------")
               }finally {
                   this@RequestSingle.runFlag += 1
               }


            }
        })
    }

    /**
     * 获取修改应用的基本信息
     */
    fun submitModify(appId:String,bundle: Bundle,coroutines: BaseActivity) = coroutines.launch {

      //  Log.d("@@modifyReceive:",bundle.toString())
        val formBody = FormBody.Builder()
            .add("appid",bundle.getString("mId")!!)
            .add("app_name",bundle.getString("mName")!!)
            .add("app_intro",bundle.getString("mDesc")!!)
            .add("app_slogo",bundle.getString("msLogoUrl")!!)
            .add("app_blogo",bundle.getString("mlLogoUrl")!!)
            .add("app_url_rd","")
            .add("app_url_th",bundle.getString("mSideAdd")!!)
            .add("app_type","iapp")
            .add("app_label_id","工具")
            .add("app_viewlevel",bundle.getString("mShowLevel")!!)
            .add("app_canweb",bundle.getString("mUseLevel")!!)
            .add("app_pro","").build()
        this@RequestSingle.client.newCall(Request.Builder().url(this@RequestSingle.modifyUrl).post(formBody).build()).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                coroutines.launch (Dispatchers.Main){
                    Tos("更新数据：网络错误！！",coroutines)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val text = response.body()!!.string()
                val doc =Jsoup.parse(text)
             //   Log.d("@@resModify:",doc.toString())
                val res = doc.select("body").html().trim()
                coroutines.launch (Dispatchers.Main){
                    if(res == "s200"){
                        Tos("修改完成！",coroutines)
                    }else{
                        val json = JSONObject(res).get("msgCN") as String

                        Tos("修改失败:$json",coroutines)
                    }
                }


            }
        })
    }







}