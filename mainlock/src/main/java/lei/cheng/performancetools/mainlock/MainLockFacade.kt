package lei.cheng.performancetools.mainlock

object MainLockFacade {
    private var config : MainLockConfig?=null

    private val manager by lazy { MainLockManager() }

    fun init(config:MainLockConfig) {
        this.config = config
        manager.initIfNeed(config)
    }
}

class MainLockConfig {
    var enable:Boolean = true
    var interval: Long = 2 * 1000L
}
