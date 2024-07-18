package lei.cheng.performancetools.mainlock.handler

import lei.cheng.performancetools.mainlock.IMainBlockHandler
import lei.cheng.performancetools.mainlock.MainLockConfig

/**
 * 主线程锁监控
 * 1. 获取主线程想要竞争的monitor：Monitor::GetContendedMonitor
 * 2. 获取这个monitor被哪个线程持有：Monitor::GetLockOwnerThreadId
 */
class MainLockBlockHandler : IMainBlockHandler {
    override fun handle(config: MainLockConfig) {

    }

    external fun queryCompeteThreadId(): Long
}