package lei.cheng.performancetools.mainlock

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    }
}