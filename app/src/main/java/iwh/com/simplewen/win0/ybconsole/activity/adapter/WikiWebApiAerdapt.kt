package iwh.com.simplewen.win0.ybconsole.activity.adapter

import iwh.com.simplewen.win0.ybconsole.YbApp
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.WikiDetail
import iwh.com.simplewen.win0.ybconsole.activity.modal.WikiWebApi

/**
 * web接口列表
 */
class WikiWebApiAerdapt(val wikiWebData:ArrayList<WikiWebApi>):RecyclerView.Adapter<WikiWebApiAerdapt.WikiWebHolder>(){
    inner class WikiWebHolder(v:View):RecyclerView.ViewHolder(v){
        val wikName = v.findViewById<TextView>(R.id.wikiWebName)//接口名
        val wikDesc = v.findViewById<TextView>(R.id.wikiWebDesc)//接口描述
        val wikInterfaceName = v.findViewById<TextView>(R.id.wikiWebInterFaceName)//接口API
    }

    override fun getItemCount(): Int  = wikiWebData.size

    override fun onBindViewHolder(holder:WikiWebHolder, position: Int) {

      holder.apply {
          wikDesc.text = wikiWebData[position].wikiDescribe
          wikName.text = wikiWebData[position].wikiName
          wikInterfaceName.text = wikiWebData[position].wikiInterfaceName
          wikInterfaceName.setOnClickListener {
            //  Tos("url：${wikiWebData[position].wikiUrl}",YbApp.getContext())
              YbApp.getContext().startActivity(Intent(YbApp.getContext(),WikiDetail::class.java).apply {
                  putExtra("apiName",wikiWebData[position].wikiName)
                  putExtra("apiUrl",wikiWebData[position].wikiUrl)
              })
          }
      }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):WikiWebHolder {
        return WikiWebHolder(LayoutInflater.from(parent.context).inflate(R.layout.wiki_web_item,null))

    }

}