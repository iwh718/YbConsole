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
import android.widget.*

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import iwh.com.simplewen.win0.ybconsole.R

import kotlinx.android.synthetic.main.activity_edit_app.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import work.RequestSingle
import java.io.File
import java.io.IOException
import java.lang.Exception


@ExperimentalCoroutinesApi
/**
 * 修改应用信息
 */
class EditApp :BaseActivity() {

    private val perArr = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var mUse: String
    private lateinit var mShow: String
    private lateinit var currentLogoUrl: Uri
    private var currentLogo = "s"
    private val mUseArr = arrayOf("仅客户端", "兼容web与客户端")
    private val mShowArr = arrayOf("所有用户", "校方认证用户", "与开发者同校用户")

    private  var newSLogoUrl: String? = null//新的小logo
    private var newLLogoUrl: String? = null//新的大logo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_app)

        modifyUse.adapter = ArrayAdapter(this@EditApp, R.layout.support_simple_spinner_dropdown_item, mUseArr)
        modifyShow.adapter = ArrayAdapter(this@EditApp, R.layout.support_simple_spinner_dropdown_item, mShowArr)

        RequestSingle.getAppInfo(intent.getStringExtra("appId"), this@EditApp, 1)


        //设置用户可见
        modifyUse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mUse = mUseArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        //设置使用场景
        modifyShow.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mShow = mShowArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        //修改logo
        editSLogo.setOnClickListener(selectLogo())
        editLLogo.setOnClickListener(selectLogo("l"))


        //提交修改
        modifySubmit.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mId", intent.getStringExtra("appId"))
                putString("mName", modifyName.text.toString())
                putString("mDesc", modifyDesc.text.toString())
                putString("msLogoUrl", if(this@EditApp.newLLogoUrl.isNullOrBlank() )RequestSingle.AppInfoData[0].sLogoUrl else newSLogoUrl)
                putString("mlLogoUrl",if(this@EditApp.newLLogoUrl.isNullOrBlank() )  RequestSingle.AppInfoData[0].lLogoUr else newLLogoUrl)
                putString("mShowLevel", "${modifyShow.selectedItemPosition}")
                putString("mUseLevel", "${modifyUse.selectedItemPosition + 1}")
                putString("mSideAdd", modifySideAdd.text.toString())
            }
            //提交修改
            RequestSingle.submitModify(intent.getStringExtra("appId"), bundle, this@EditApp)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //选择图片
                0x11 -> {
                    if (data != null) {
                        this@EditApp.corpImg(data.data!!)
                    }else{
                        Tos("返回数据为空！",this@EditApp)
                    }
                }

                //小logo
                0x12 -> {
                      if (data != null) {

                    editSLogo.setImageURI(currentLogoUrl)
                          //上传小Logo
                          upImg(type = "logo_64")

                     } else {
                       Tos("data为空！", this@EditApp)
                      }
                }
                //大logo
                0x13 -> {

                      if (data != null) {

                    editLLogo.setImageURI(currentLogoUrl)
                          upImg("logo_108")

                     } else {
                      Tos("data为空！", this@EditApp)
                      }
                }
            }
        } else {
            Tos("获取失败，请重新获取！", this@EditApp)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0x13) {
            if (ContextCompat.checkSelfPermission(
                    this@EditApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Tos("授权完成！", this@EditApp)
                startActivityForResult(Intent().apply {
                    action = Intent.ACTION_PICK
                    setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                }, 0x11)
            } else {
                Tos("未授权！", this@EditApp)
            }

        }

    }


    //裁切图片
    private fun corpImg(uri: Uri) {

        currentLogoUrl =
            Uri.parse("file:///" + Environment.getExternalStorageDirectory().path + "/${this@EditApp.currentLogo}Logo.png")
        Intent().apply {
            action = "com.android.camera.action.CROP"
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("scale", true)

            putExtra("aspectX", 1)
            putExtra("aspectY", 1)

            putExtra("outputX", if (this@EditApp.currentLogo == "s") 64 else 108)
            putExtra("outputY", if (this@EditApp.currentLogo == "s") 64 else 108)

            putExtra("return-data", false)
            putExtra(MediaStore.EXTRA_OUTPUT, currentLogoUrl)
            putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            startActivityForResult(this, if (this@EditApp.currentLogo == "s") 0x12 else 0x13)

        }
    }

    //修改logo
    private fun selectLogo(type: String = "s"): View.OnClickListener {

        return View.OnClickListener {
            this@EditApp.currentLogo = type
            if (ContextCompat.checkSelfPermission(
                    this@EditApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivityForResult(Intent().apply {
                    action = Intent.ACTION_PICK
                    setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                }, 0x11)
            } else {
                Tos("权限未授予！", this@EditApp)
                ActivityCompat.requestPermissions(this@EditApp, perArr, 0x13)
            }
        }


    }



    private fun upImg(type:String = "logo_64") = this@EditApp.launch{

            val logoFile = File(currentLogoUrl.path)
            val requestImg = RequestBody.create(MediaType.parse("image/png"), logoFile)
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file",currentLogoUrl.path,requestImg).build()
            RequestSingle.client.newCall(Request.Builder().url("https://o.yiban.cn/ajax/upload?t=$type").post(requestBody).build())
                .enqueue(object :Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        launch (Dispatchers.Main){
                            Tos("上传Logo：网络错误！",this@EditApp)
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val re = response.body()!!.string()
                        Log.d("@@upImg:",re)
                        this@EditApp.launch(Dispatchers.Main) {
                            try {
                                if(JSONObject(re).get("code") != "s200"){
                                    Tos(JSONObject(re).get("msgCN") as String,this@EditApp)
                                }else{
                                    Tos("上传完成：${JSONObject(re).get("web_url")}",this@EditApp)
                                    if(type == "logo_108") this@EditApp.newLLogoUrl = JSONObject(re).get("web_url") as String else this@EditApp.newSLogoUrl = JSONObject(re).get("web_url") as String

                                }
                            }catch (e:Exception){
                                Tos("上传错误，稍后重试！",this@EditApp)
                            }
                        }
                    }

                })


    }
}
