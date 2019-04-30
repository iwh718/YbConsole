package App

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

/**
 * 全局变量
 */
class YbApp : Application() {
    companion object {
        var Ycontext: Application by Delegates.notNull()
        fun getContext(): Context = Ycontext
    }

    override fun onCreate() {
        super.onCreate()
        Ycontext = this
    }

}