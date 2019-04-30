package iwh.com.simplewen.win0.ybconsole.activity.adapter

import android.os.UserManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * ViewPage适配器
 */
class ViewPageAdapter(private val fm: FragmentManager,private val fgList:ArrayList<Fragment>): FragmentPagerAdapter(fm){
    override fun getCount(): Int {
        return fgList.size
    }

    override fun getItem(position: Int): Fragment {
       return fgList[position]
    }

}