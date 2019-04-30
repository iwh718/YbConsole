package Utils

import android.content.Context
import android.widget.Toast

fun Tos(str:String,context: Context){
    Toast.makeText(context,str,Toast.LENGTH_SHORT).show()
}