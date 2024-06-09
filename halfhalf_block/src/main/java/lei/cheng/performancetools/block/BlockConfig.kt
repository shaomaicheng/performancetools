package lei.cheng.performancetools.block

import android.app.Application

/**
 * @author halflinecode
 * @date 2024/5/26
 * @time 22:12
 */
class BlockConfig(val application: Application) {
    var blockCheckInterval: Long = 0L
    var longTimeMsgThreshold:Long=0L
    var traceInterval = 10L// 采集trace间隔,ms
    var dir = "" // 堆栈文件目录
}