package Utils

import android.content.Context
import android.widget.Toast
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

fun Tos(str:String,context: Context){
    Toast.makeText(context,str,Toast.LENGTH_SHORT).show()
}
@ExperimentalCoroutinesApi

fun showNetError(coroutines:BaseActivity,type:String = "意外") = coroutines.launch(Dispatchers.Main){
    Tos("$type:网络出错！",coroutines)
}