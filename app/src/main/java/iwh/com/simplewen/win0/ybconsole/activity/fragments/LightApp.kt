package iwh.com.simplewen.win0.ybconsole.activity.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.AddLightApp
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.adapter.LightRecycleAdapter
import kotlinx.android.synthetic.main.light_app_fragment.view.*
import kotlinx.coroutines.*
import work.RequestSingle

/**
 * 轻应用
 */
@ExperimentalCoroutinesApi
class LightApp : Fragment() {
    lateinit var coroutines: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        //开始获取轻应用数据
        RequestSingle.getManage(this@LightApp.coroutines)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val ly = inflater.inflate(R.layout.light_app_fragment, null)
        ly.lightRefresh.isRefreshing = true
        ly.lightRecycle.adapter = LightRecycleAdapter(RequestSingle.LightDataNoAuth,coroutines)
       // Log.d("@@初始化Adapter","---------------")
        ly.addLight.setOnClickListener {
            coroutines.startActivity(Intent(coroutines,AddLightApp::class.java).apply {
                flags  = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        ly.lightRecycle.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        ly.lightRefresh.setOnRefreshListener {
            RequestSingle.getManage(coroutines)
        }

        return ly
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.coroutines = context as BaseActivity

    }


}