package com.example.jvmtidmeo

import android.app.Application
import lei.cheng.performancetools.PerformanceToolsManager

/**
 * @author chenglei01
 * @date 2023/9/16
 * @time 17:51
 */
class MineApp:Application() {
    override fun onCreate() {
        super.onCreate()
        PerformanceToolsManager().init(this)
    }
}