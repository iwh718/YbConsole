package iwh.com.simplewen.win0.ybconsole.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.adapter.MsgBoxListAdapter
import kotlinx.android.synthetic.main.activity_msg_box.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import work.RequestSingle

/**
 * 消息页面
 */
@ExperimentalCoroutinesApi
class MsgBox : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg_box)
        msgBoxRecycle.adapter = MsgBoxListAdapter(RequestSingle.MsgBoxData,this@MsgBox)
        msgBoxRecycle.layoutManager = StaggeredGridLayoutManager(1,RecyclerView.VERTICAL)
        RequestSingle.getMsgBoxList(this@MsgBox)

    }
}
