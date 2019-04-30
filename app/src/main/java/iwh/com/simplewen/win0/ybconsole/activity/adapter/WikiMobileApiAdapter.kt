package iwh.com.simplewen.win0.ybconsole.activity.adapter


import App.YbApp
import Utils.Tos
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.WikiDetail
import iwh.com.simplewen.win0.ybconsole.activity.modal.WikiMobileApi


/**
 * mobile端api列表
 */
class WikiMobileApiAdapter(private val wikiMobileData:ArrayList<WikiMobileApi>):RecyclerView.Adapter<WikiMobileApiAdapter.WikiMobileHolder>(){
    inner class WikiMobileHolder(view: View):RecyclerView.ViewHolder(view){
        val wiName = view.findViewById<TextView>(R.id.wikiMobileName)
        val wiDesd = view.findViewById<TextView>(R.id.wikiMobileDesc)
    }

    override fun getItemCount(): Int  = wikiMobileData.size

    override fun onBindViewHolder(holder: WikiMobileHolder, position: Int) {
        holder.wiName.text = wikiMobileData[position].wikiName
        holder.wiDesd.text = wikiMobileData[position].wikiDescribe
        holder.wiName.setOnClickListener {
           // Tos("url：${wikiMobileData[position].wikiUrl}",YbApp.getContext())
            val intent = Intent(YbApp.getContext(),WikiDetail::class.java)
            intent.putExtra("apiName",wikiMobileData[position].wikiName)
            intent.putExtra("apiUrl",wikiMobileData[position].wikiUrl)
            YbApp.getContext().startActivity(intent)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WikiMobileHolder {
        return WikiMobileHolder(LayoutInflater.from(parent.context).inflate(R.layout.wiki_mobile_item,null))

    }
}