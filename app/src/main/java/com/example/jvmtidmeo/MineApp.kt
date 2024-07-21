package com.example.jvmtidmeo

import android.app.Application
import android.util.Log
import lei.cheng.performancetools.PerformanceToolsManager
import lei.cheng.performancetools.block.BlockConfig
import lei.cheng.performancetools.block.BlockMonitor
import lei.cheng.performancetools.mainlock.ILockResult
import lei.cheng.performancetools.mainlock.MainLockConfig
import lei.cheng.performancetools.mainlock.MainLockFacade
import lei.cheng.performancetools.mainlock.MainLockManager
import lei.cheng.performancetools.mainlock.handler.ThreadLockMeta

/**
 * @author chenglei01
 * @date 2023/9/16
 * @time 17:51
 */
class MineApp:Application() {
    override fun onCreate() {
        super.onCreate()
        BlockMonitor.init(BlockConfig(this).apply {
            this.blockCheckInterval = 2 * 1000L
            this.msgBlockCount = 3
            this.traceInterval = 50
        })
        MainLockFacade.init(MainLockConfig().apply {
            this.handleResult = object : ILockResult {
                override fun handleResult(results: List<ThreadLockMeta>) {
                    results.forEachIndexed { index, meta ->
                        if (index != results.size - 1) {
                            val target = results[index+1]
                            Log.e("halfline", "线程${meta.name}等待锁，锁目前被${target.name}持有")
                            Log.e("halfline","=============线程${meta.name}堆栈如下=============")
                            Log.e("halfline", meta.traces)
                            Log.e("halfline","=============线程${target.name}堆栈如下=============")
                            Log.e("halfline", target.traces)
                        }
                    }
                }

            }
        })
        PerformanceToolsManager().init(this)
    }
}