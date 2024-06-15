package lei.cheng.performancetools.block

import android.app.Application
import lei.cheng.performancetools.block.debug.BlockTraceDB

/**
 * @author halflinecode
 * @date 2024/5/26
 * @time 22:12
 */
class BlockConfig(val application: Application) {
    var blockCheckInterval: Long = 0L
    var longTimeMsgThreshold: Long = 0L
    var traceInterval = 10L// 采集trace间隔,ms
    var msgBlockCount = 1 // 主线程同一条消息不处理几次后认定为卡顿
    var dir = "" // 堆栈文件目录
    var uploader: IBlockTraceReport? = null
    var db : IBlockDB? = BlockTraceDB(application)
}