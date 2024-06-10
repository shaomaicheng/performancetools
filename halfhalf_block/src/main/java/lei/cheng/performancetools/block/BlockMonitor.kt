package lei.cheng.performancetools.block

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * @author halflinecode
 * @date 2024/5/26
 * @time 19:35
 */
object BlockMonitor {

    private var background = MutableLiveData<Boolean>()
    lateinit var config: BlockConfig
        private set
    private lateinit var dogWatcher : BlockWatchDog

    init {
        background.observeForever {
            if (it) {
                dogWatcher.start()
            } else {
                dogWatcher.stop()
            }
        }
    }

    fun init(config: BlockConfig) {
        this.config = config
        this.dogWatcher = BlockWatchDog(this.config)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        background.value = true
                    }

                    Lifecycle.Event.ON_STOP -> {
                        background.value = false
                    }

                    else -> {}
                }
            }

        })
    }
}