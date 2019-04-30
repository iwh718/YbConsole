package iwh.com.simplewen.win0.ybconsole.activity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import iwh.com.simplewen.win0.ybconsole.R
import iwh.com.simplewen.win0.ybconsole.activity.BaseActivity
import iwh.com.simplewen.win0.ybconsole.activity.adapter.WikiMobileApiAdapter
import iwh.com.simplewen.win0.ybconsole.activity.adapter.WikiWebApiAerdapt
import kotlinx.android.synthetic.main.wiki_fragment.view.*

import kotlinx.coroutines.ExperimentalCoroutinesApi
import work.RequestSingle

/**
 * wiki页面
 */
@ExperimentalCoroutinesApi
class WikiManage:Fragment(){
    private lateinit var coroutines:BaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RequestSingle.getWiki(coroutines)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ly = inflater.inflate(R.layout.wiki_fragment,null)
        val lym  = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        val lyh = LinearLayoutManager(coroutines).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        ly.wikiMobileApiRecycle.apply {
            adapter = WikiMobileApiAdapter(RequestSingle.wikiMobileData)
            layoutManager = lyh
        }
        ly.wikiWebApiRecycle.apply {
            adapter = WikiWebApiAerdapt(RequestSingle.wikiWebApiData)
            layoutManager = lym
        }

        return ly
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.coroutines = context as BaseActivity


    }
}