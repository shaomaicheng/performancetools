package lei.cheng.performancetools.block

import android.util.Log

/**
 * @author chenglei01
 * @date 2024/6/8
 * @time 22:29
 */
class Logger {
    companion object {
        fun log(tag: String, log:String) {
            if (true){
                Log.e(tag,log)
            }
        }
    }
}