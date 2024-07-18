package lei.cheng.performancetools.mainlock

import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lei.cheng.performancetools.mainlock.handler.MainLockBlockHandler
import java.lang.Thread.State

class MainLockManager {

    private var init=false
    private var running=false
    private lateinit var config:MainLockConfig

    private val mainLockBlockHandler = MainLockBlockHandler()

    fun initIfNeed(config: MainLockConfig) {
        if (init){
            return
        }
        this.config = config
        init=true
    }

    private fun start() {
        if (running) return
        running = true
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            // read main Thread status
            readMainThreadStatus()
            delay(config.interval)
        }
    }

    private fun readMainThreadStatus() {
        val mainThread = Looper.getMainLooper().thread
        val isBlock = mainThread.state == State.BLOCKED
        when {
            !mainThread.isAlive->{
                return
            }
            isBlock->{
                mainLockBlockHandler.handle(config)
            }

        }
    }
}