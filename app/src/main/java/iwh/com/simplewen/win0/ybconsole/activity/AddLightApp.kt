package iwh.com.simplewen.win0.ybconsole.activity

import Utils.Tos
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

import iwh.com.simplewen.win0.ybconsole.R
import kotlinx.android.synthetic.main.activity_add_light_app.*

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 添加新的轻应用
 */
@ExperimentalCoroutinesApi
class AddLightApp : BaseActivity() {
    private val addShowArr = arrayOf("所有用户", "校方认证用户", "与开发者同校用户")
    private val addUseArr = arrayOf("仅客户端", "兼容web与客户端")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_light_app)

        addNextBtn.setOnClickListener{
            AlertDialog.Builder(this@AddLightApp).setView(EditText(this@AddLightApp).apply {
                this.hint = "应用站外地址"
            }).setPositiveButton("提交"){
                _,_ ->
                cardTop.visibility = View.GONE
                cardBottom.visibility = View.GONE
                addLightSubmit.show()
                Tos("创建完成！",this@AddLightApp)
                addLightSubmit.setOnClickListener{
                    startActivity(Intent(this@AddLightApp,MainActivity::class.java))
                    finish()
                }
            }
                .setNegativeButton("等一下",null).create().show()
        }

        //可见
        addLightShow.adapter = ArrayAdapter(this@AddLightApp,R.layout.support_simple_spinner_dropdown_item,addShowArr)

        //场景
        addLightUse.adapter = ArrayAdapter(this@AddLightApp,R.layout.support_simple_spinner_dropdown_item,addUseArr)

        addLightShow.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Tos(addShowArr[position],this@AddLightApp)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        addLightUse.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Tos(addUseArr[position],this@AddLightApp)
            }

            override fun onNothingSelected(parent: AdapterView<*>?)  = Unit
        }
    }
}
