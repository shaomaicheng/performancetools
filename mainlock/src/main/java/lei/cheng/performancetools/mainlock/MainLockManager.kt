package lei.cheng.performancetools.mainlock

import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lei.cheng.performancetools.mainlock.handler.MainLockBlockHandler
import java.lang.Thread.State

class MainLockManager {

    @Volatile
    private var init=false

    @Volatile
    private var running=false

    private lateinit var config:MainLockConfig

    private val mainLockBlockHandler = MainLockBlockHandler()

    fun initIfNeed(config: MainLockConfig) {
        if (!config.enable) return
        if (init) {
            return
        }
        init()
        this.config = config
        init=true
        start()
    }

    private fun start() {
        if (running) return
        running = true
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            // read main Thread status
            while (true) {
                readMainThreadStatus()
                delay(config.interval)
            }
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


    companion object {
        val lock = 1
        fun init() {
            System.loadLibrary("mainlockblock")
            native_init()
        }
        fun test(){
            synchronized(lock){
                val thread= Looper.getMainLooper().thread
                val io = Thread {
                    synchronized(lock) {
                        Log.e("chenglei","xxx")
                    }
                }.apply {
                    start()
                }
                val field = Thread::class.java.getDeclaredField("nativePeer").apply { this.isAccessible=true }
                Log.e("chenglei","injava:${field.get(thread) as Long},${field.get(io) as Long},${thread.id}, ${io.id}")
                test1(field.get(thread) as Long, field.get(io) as Long)
            }
        }
        external fun test1(thread:Long,otherthread:Long)
        private external fun native_init()
    }
}