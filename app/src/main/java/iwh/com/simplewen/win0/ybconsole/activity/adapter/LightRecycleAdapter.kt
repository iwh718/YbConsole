package iwh.com.simplewen.win0.ybconsole.activity.adapter

import iwh.com.simplewen.win0.ybconsole.YbApp
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions

import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.AppInfo
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.EditApp
import iwh.com.simplewen.win0.ybconsole.activity.modal.LightItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import work.RequestSingle

/**
 * 轻应用列表
 */
@ExperimentalCoroutinesApi
class LightRecycleAdapter(private val lightApp: ArrayList<LightItem>,private val coroutines:BaseActivity) :
    RecyclerView.Adapter<LightRecycleAdapter.LightRecycleViewHolder>() {

    inner class LightRecycleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val itemName = view.findViewById<TextView>(R.id.lightItemName)
        var itemLogo = view.findViewById<ImageView>(R.id.lightItemLogo)
        var itemLevel = view.findViewById<TextView>(R.id.lightItemLevel)
        var itemStatus = view.findViewById<TextView>(R.id.lightItemStatus)
        var itemMore = view.findViewById<ImageView>(R.id.lightMore)


    }

    override fun getItemCount(): Int {
        return lightApp.size
    }

    override fun onBindViewHolder(holder: LightRecycleViewHolder, position: Int) {
        fun  toMore(){
            YbApp.getContext().startActivity(Intent(YbApp.getContext(), AppInfo::class.java).apply {
                putExtra("appId",lightApp[position].itemUrl)
                putExtra("appLogoUrl",lightApp[position].itemLogoUrl)
                putExtra("appName",lightApp[position].itemName)
            })
        }
        holder.itemLevel.text = "权限等级：${lightApp[position].itemLevel}"
        holder.itemName.text = "应用：${lightApp[position].itemName}"
        holder.itemStatus.text = "状态：${lightApp[position].itemStatus}"
        holder.itemMore.apply {
            setOnClickListener {
                PopupMenu(YbApp.getContext(), this).apply inner@{

                    this@inner.menuInflater.inflate(R.menu.light_menu, this.menu)
                    this@inner.setOnMenuItemClickListener {
                        when(it.itemId){
                            //查看详情
                            R.id.lightPopMore ->{
                               // Tos(lightApp[position].itemUrl,YbApp.getContext())

                              toMore()
                                true

                            }
                            //调试
                            R.id.lightPopDebug->{
                              //  Tos(lightApp[position].itemUrl,YbApp.getContext())
                                RequestSingle.getDebugImg(coroutines,lightApp[position].itemUrl)
                                true
                            }
                            //修改
                            R.id.lightPopEdit -> {
                                YbApp.getContext().startActivity(Intent(YbApp.getContext(),EditApp::class.java).apply{
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    putExtra("appId",lightApp[position].itemUrl)


                                })
                                true
                            }
                            else-> true
                        }
                    }
                    this@inner.show()
                }
            }
        }
        Glide.with(holder.itemLevel).load(lightApp[position].itemLogoUrl).apply(RequestOptions.bitmapTransform(CircleCrop())).into(holder.itemLogo)
        holder.itemLogo.setOnClickListener {
            toMore()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightRecycleViewHolder {
        return LightRecycleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.light_app_item, null))
    }
}