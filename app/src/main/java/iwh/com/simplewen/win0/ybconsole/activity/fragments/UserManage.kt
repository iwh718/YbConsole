package iwh.com.simplewen.win0.ybconsole.activity.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.Login
import kotlinx.android.synthetic.main.user_fragment.view.*

import kotlinx.coroutines.ExperimentalCoroutinesApi

import work.RequestSingle

/**
 * 个人信息
 */
@ExperimentalCoroutinesApi
class UserManage: Fragment(){

    private lateinit var coroutines:BaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取基本信息
       RequestSingle.getUserInfo(coroutines)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.user_fragment,null).apply {
            this.userExit.setOnClickListener{
                val sf = coroutines.getSharedPreferences("loginInfo", Context.MODE_PRIVATE)
                sf.edit().putString("flag","0").apply()
                coroutines.startActivity(Intent(coroutines,Login::class.java))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.coroutines = context as BaseActivity
    }
}