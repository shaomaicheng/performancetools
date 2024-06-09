package lei.cheng.performancetools.block

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author halflinecode
 * @date 2024/5/26
 * @time 19:42
 */
class BlockWatchDog(private val config: BlockConfig) {
    private var watchJob: Job? = null
    private var running = false
    private val checkQueue = arrayListOf<Long>()
    private var blockMsg: Message? = null
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Logger.log("halfline", "消息被处理,what:${msg.what}.obj:${msg.obj}")
            when (msg.what) {
                WHAT_CHECK -> {
                    checkQueue.removeFirst()
                    val blockTime = blockMsg?.obj as? Long
                    val msgTime = msg.obj as? Long

                    if (blockTime != null && msgTime != null) {
                        if (blockTime <= msgTime) {
                            //卡顿消息时间小于等于处理消息时间，卡顿结束
                            MainTracer.stop()
                        }
                    }
                }
            }
        }
    }

    fun start() {
        if (running) {
            return
        }
        running = true
        Logger.log("halfline", "开始卡顿监控")
        watchJob =
            ProcessLifecycleOwner.get().lifecycleScope.launch(SupervisorJob() + Dispatchers.IO) {
                while (true) {
                    postCheck()
                    delay(config.blockCheckInterval)
                    checkBlock { time ->
                        Logger.log("halfline", "发现卡顿")
                    }
                }
            }
    }

    fun stop() {
        if (blockMsg != null) {
            return
        }
        watchJob?.cancel()
        running = false
    }

    private fun postCheck() {
        val time = SystemClock.elapsedRealtime()
        val message = Message.obtain()
        message.what = WHAT_CHECK
        message.obj = time
        checkQueue.add(time)
        handler.sendMessage(message)
        Logger.log("halfline", "发送消息")
    }

    private fun checkBlock(blockCallback: (Long) -> Unit) {
        Logger.log("halfline", "检查")
        val sendTime = if (checkQueue.isNotEmpty()) checkQueue.first() else 0L //获取第一个未处理的消息发送时间
        if (sendTime == 0L) return
        val looper = handler.looper
        val messageQueue = looper.queue
        kotlin.runCatching {
            val mqClass = Class.forName("android.os.MessageQueue")
            val message = mqClass.declaredFields.find { field ->
                field.name == FIELD_M_MESSAGE
            }
            message?.isAccessible = true
            val mMessage = message?.get(messageQueue) as? Message
            var next: Message?
            if (mMessage != null) {
                next = mMessage
                while (true) {
                    if (next == null) break
                    if (next.what == WHAT_CHECK) {
                        val time = next.obj as? Long
                        Log.e("halfline", "头部时间:${sendTime},msg时间:${time}")
                        if (time == sendTime) {
                            // 第一个check消息的时间和消息时间一样，说明卡顿
                            blockMsg = next
                            MainTracer.start(config, sendTime)
                            blockCallback(time)
                            break
                        }
                    }
                    next = nextMessage(next)
                }
            }
        }.getOrElse { e ->
            Logger.log("halfline", "异常:$e")
        }
    }

    private fun nextMessage(message: Message): Message? {
        val messageClazz = Class.forName("android.os.Message")
        val nextField = messageClazz.declaredFields.find {
            it.name == FIELD_NEXT
        }
        nextField?.isAccessible = true
        return nextField?.get(message) as? Message
    }

    companion object {
        private const val WHAT_CHECK = 666
        private const val FIELD_M_MESSAGE = "mMessages"
        private const val FIELD_NEXT = "next"

    }
}
