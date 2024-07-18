package com.example.jvmtidmeo

import android.app.Application
import lei.cheng.performancetools.PerformanceToolsManager
import lei.cheng.performancetools.block.BlockConfig
import lei.cheng.performancetools.block.BlockMonitor
import lei.cheng.performancetools.mainlock.MainLockManager

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
        MainLockManager.init()
        PerformanceToolsManager().init(this)
    }
}