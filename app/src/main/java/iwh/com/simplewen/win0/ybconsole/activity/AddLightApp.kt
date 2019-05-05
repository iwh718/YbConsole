package iwh.com.simplewen.win0.ybconsole.activity

import Utils.Tos
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import iwh.com.simplewen.win0.ybconsole.R
import kotlinx.android.synthetic.main.activity_add_light_app.*
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import work.RequestSingle
import java.io.File
import java.io.IOException
import java.lang.Exception

/**
 * 添加新的轻应用
 */
@ExperimentalCoroutinesApi
class AddLightApp : BaseActivity() {
    //用户可见度等级
    private var addLightShowLevel: String = "0"
    //logo地址
    private var addSLogoUrl: String? = null
    private var addLLogoUrl: String? = null
    private val perArr = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var currentLogoUrl: Uri
    private var currentLogo = "s"
    private val addShowArr = arrayOf("所有用户", "校方认证用户", "与开发者同校用户")

    private lateinit var mUrlTv: EditText//站内地址输入框
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_light_app)
        //下一步
        addNextBtn.setOnClickListener {
            mUrlTv = EditText(this@AddLightApp)
            if (!addLightName.text.isNullOrBlank() && !addLightDesc.text.isNullOrBlank() && !this@AddLightApp.addSLogoUrl.isNullOrBlank() && !addLLogoUrl.isNullOrBlank()) {
                AlertDialog.Builder(this@AddLightApp).setView(mUrlTv.apply {
                    this.hint = "应用站外地址"
                }).setPositiveButton("提交") { _, _ ->
                    if (!mUrlTv.text.isNullOrBlank()) {
                        val bundle = Bundle().apply {
                            putString("mName", addLightName.text.toString())
                            putString("mDesc", addLightDesc.text.toString())
                            putString("mUrl",this@AddLightApp.mUrlTv.text.toString())
                            putString("msLogoUrl", this@AddLightApp.addSLogoUrl)
                            putString("mlLogoUrl", this@AddLightApp.addLLogoUrl)
                            putString("mShowLevel", this@AddLightApp.addLightShowLevel)
                        }
                        //请求添加
                        upPro.visibility = View.VISIBLE
                        RequestSingle.addApp(bundle, this@AddLightApp)
                        cardTop.visibility = View.GONE
                        it.visibility = View.INVISIBLE

                        addLightSubmit.setOnClickListener {
                            startActivity(Intent(this@AddLightApp, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                    .setNegativeButton("等一下", null).create().show()
            } else {
                Tos("请补全信息再提交！", this@AddLightApp)
            }
        }
        //添加小图标
        addSLogo.setOnClickListener(selectLogo())
        //添加大图标
        addLLogo.setOnClickListener(selectLogo("l"))

        //可见
        addLightShow.adapter = ArrayAdapter(this@AddLightApp, R.layout.support_simple_spinner_dropdown_item, addShowArr)


        addLightShow.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                this@AddLightApp.addLightShowLevel = "$position"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0x13) {
            if (ContextCompat.checkSelfPermission(
                    this@AddLightApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Tos("授权完成！", this@AddLightApp)
                startActivityForResult(Intent().apply {
                    action = Intent.ACTION_PICK
                    setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                }, 0x11)
            } else {
                Tos("未授权！", this@AddLightApp)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //选择图片
                0x11 -> {
                    if (data != null) {
                        this@AddLightApp.corpImg(data.data!!)
                    } else {
                        Tos("返回数据为空！", this@AddLightApp)
                    }
                }

                //小logo
                0x12 -> {
                    if (data != null) {

                        addSLogoImg.setImageURI(currentLogoUrl)
                        //上传小Logo
                        upImg(type = "logo_64")

                    } else {
                        Tos("data为空！", this@AddLightApp)
                    }
                }
                //大logo
                0x13 -> {

                    if (data != null) {

                        addLLogoImg.setImageURI(currentLogoUrl)
                        upImg("logo_108")

                    } else {
                        Tos("data为空！", this@AddLightApp)
                    }
                }
            }
        } else {
            Tos("获取失败，请重新获取！", this@AddLightApp)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    //裁切图片
    private fun corpImg(uri: Uri) {

        currentLogoUrl =
            Uri.parse("file:///" + Environment.getExternalStorageDirectory().path + "/${this@AddLightApp.currentLogo}Logo.png")
        Intent().apply {
            action = "com.android.camera.action.CROP"
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("scale", true)

            putExtra("aspectX", 1)
            putExtra("aspectY", 1)

            putExtra("outputX", if (this@AddLightApp.currentLogo == "s") 64 else 108)
            putExtra("outputY", if (this@AddLightApp.currentLogo == "s") 64 else 108)

            putExtra("return-data", false)
            putExtra(MediaStore.EXTRA_OUTPUT, currentLogoUrl)
            putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            startActivityForResult(this, if (this@AddLightApp.currentLogo == "s") 0x12 else 0x13)

        }
    }

    //修改logo
    private fun selectLogo(type: String = "s"): View.OnClickListener {

        return View.OnClickListener {
            this@AddLightApp.currentLogo = type
            if (ContextCompat.checkSelfPermission(
                    this@AddLightApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivityForResult(Intent().apply {
                    action = Intent.ACTION_PICK
                    setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                }, 0x11)
            } else {
                Tos("权限未授予！", this@AddLightApp)
                ActivityCompat.requestPermissions(this@AddLightApp, perArr, 0x13)
            }
        }


    }

    private fun upImg(type: String = "logo_64") = this@AddLightApp.launch {

        val logoFile = File(currentLogoUrl.path)
        val requestImg = RequestBody.create(MediaType.parse("image/png"), logoFile)
        val requestBody =
            MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", currentLogoUrl.path, requestImg)
                .build()
        RequestSingle.client.newCall(Request.Builder().url("https://o.yiban.cn/ajax/upload?t=$type").post(requestBody).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    launch(Dispatchers.Main) {
                        Tos("上传Logo：网络错误！", this@AddLightApp)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val re = response.body()!!.string()
                    Log.d("@@upImg:", re)
                    this@AddLightApp.launch(Dispatchers.Main) {
                        try {
                            if (JSONObject(re).get("code") != "s200") {
                                Tos(JSONObject(re).get("msgCN") as String, this@AddLightApp)
                            } else {
                                Tos("上传完成：${JSONObject(re).get("web_url")}", this@AddLightApp)
                                if (type == "logo_108") this@AddLightApp.addLLogoUrl =
                                    JSONObject(re).get("web_url") as String else this@AddLightApp.addSLogoUrl =
                                    JSONObject(re).get("web_url") as String

                            }
                        } catch (e: Exception) {
                            Tos("上传错误，稍后重试！", this@AddLightApp)
                        }
                    }
                }

            })


    }
}
