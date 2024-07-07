package lei.cheng.performancetools.mainlock

import android.os.Looper
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.State

class MainLockManager {

    private var running=false

    fun start(config: MainLockConfig) {
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
        val isWaiting = mainThread.state == State.WAITING
    }
}