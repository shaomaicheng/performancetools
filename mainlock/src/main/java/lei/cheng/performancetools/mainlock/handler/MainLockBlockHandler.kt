package lei.cheng.performancetools.mainlock.handler

import android.os.Looper
import lei.cheng.performancetools.mainlock.IMainBlockHandler
import lei.cheng.performancetools.mainlock.MainLockConfig
import java.util.LinkedList

/**
 * 主线程锁监控
 * 1. 获取主线程想要竞争的monitor：Monitor::GetContendedMonitor
 * 2. 获取这个monitor被哪个线程持有：Monitor::GetLockOwnerThreadId
 */
class MainLockBlockHandler : IMainBlockHandler {

    private val dumper by lazy {
        ThreadDumper()
    }

    override fun handle(config: MainLockConfig) {
        val mainThread = Looper.getMainLooper().thread
        if (mainThread.state != Thread.State.BLOCKED) {
            return
        }
        val threads = dumper.getAllThreads()
        val links = LinkedList<ThreadLockMeta>()
        analysis(mainThread, threads, links)
        config.handleResult?.handleResult(links)
    }

    private fun analysis(thread: Thread, threads:List<Thread>, links:LinkedList<ThreadLockMeta>) {
        val peer = thread.peer()
        if (peer == 0L) {
            return
        }
        val competeThreadId = queryCompeteThreadId(peer)
        if (competeThreadId == 0) {
            //没有竞争的线程，结束
            return
        }
        links.add(ThreadLockMeta(thread))
        val targetThread = threads.find { competeThreadId == threadIdByPtr(it.peer()) }
        if (targetThread != null) {
            // 递归分析
            links.add(ThreadLockMeta(targetThread))
            analysis(targetThread, threads, links)
        }
    }


    private external fun queryCompeteThreadId(threadId: Long): Int
    private external fun threadIdByPtr(ptr:Long): Int
}

fun Thread.peer() : Long {
    var result = 0L
    kotlin.runCatching {
        val peerField = Thread::class.java.getDeclaredField("nativePeer").apply {
            isAccessible = true
        }
        result = peerField.get(this) as? Long ?: 0L
    }
    return result
}