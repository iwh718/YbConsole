package iwh.com.simplewen.win0.ybconsole.activity.adapter


import Utils.Tos
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.modal.MsgBoxList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import work.RequestSingle


/**
 * 消息列表适配器
 */
@ExperimentalCoroutinesApi
class MsgBoxListAdapter(val msgBoxData: ArrayList<MsgBoxList>,val coroutines:BaseActivity) : RecyclerView.Adapter<MsgBoxListAdapter.MsgBoxHolder>() {
    inner class MsgBoxHolder(v: View) : RecyclerView.ViewHolder(v) {
        val msgName = v.findViewById<TextView>(R.id.msgTitle)//标题
        val msgTime = v.findViewById<TextView>(R.id.msgTime)//时间戳
        val msgBtn = v.findViewById<Button>(R.id.msgBtn)

    }

    override fun getItemCount(): Int = msgBoxData.size

    override fun onBindViewHolder(holder: MsgBoxHolder, position: Int) {

        holder.apply {
            msgName.text = msgBoxData[position].msgName
            msgTime.text = msgBoxData[position].msgTime
            msgBtn.setOnClickListener {
                //Tos(msgBoxData[position].msgId,coroutines)
                RequestSingle.getMsgInfo(msgBoxData[position].msgId,coroutines)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgBoxHolder {
        return MsgBoxHolder(LayoutInflater.from(parent.context).inflate(R.layout.msg_list_item, null))

    }

}