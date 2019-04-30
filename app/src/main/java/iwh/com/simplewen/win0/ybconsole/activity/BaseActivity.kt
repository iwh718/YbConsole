package iwh.com.simplewen.win0.ybconsole.activity

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

@ExperimentalCoroutinesApi
open class  BaseActivity:AppCompatActivity(),CoroutineScope by MainScope(){

    override fun onDestroy() {
        super.onDestroy()
        this@BaseActivity.cancel()
    }
}