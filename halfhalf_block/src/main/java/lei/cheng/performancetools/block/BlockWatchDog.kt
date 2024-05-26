package lei.cheng.performancetools.block

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
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
    private var watchJob : Job? = null
    private val checkQueue = arrayListOf<Long>()
    private val handler = Handler(Looper.getMainLooper())

    fun start() {
        watchJob = ProcessLifecycleOwner.get().lifecycleScope.launch(SupervisorJob() + Dispatchers.IO) {
            while (true) {
                postCheck()
                delay(config.interval)
                checkBlock {time->
                    Log.e("halfline", "发现卡顿")
                }
            }
        }
    }

    fun stop() {
        watchJob?.cancel()
    }

    private fun postCheck() {
        val time = SystemClock.elapsedRealtime()
        val message = Message.obtain()
        message.what = WHAT_CHECK
        message.obj = time
        checkQueue.add(time)
        handler.sendMessage(message)
    }

    private fun checkBlock(blockCallback:(Long)->Unit) {
        val sendTime = if (checkQueue.isNotEmpty()) checkQueue.first() else 0L
        if (sendTime == 0L) return
        val looper = handler.looper
        val messageQueue = looper.queue
        kotlin.runCatching {
            val mqClass = messageQueue::class.java
            val message = mqClass.declaredFields.find { field->
                field.name == FIELD_M_MESSAGE
            }
            val mMessage = message?.get(mqClass) as? Message
            var next: Message?
            if (mMessage != null) {
                next = mMessage
                while (true) {
                    if (next == null) break
                    if (next.what == WHAT_CHECK) {
                        val time = mMessage.obj as? Long
                        if (time == sendTime) {
                            blockCallback(time)
                            checkQueue.removeFirst()
                        }
                    }
                    next = nextMessage(next)
                }
            }
        }
    }

    private fun nextMessage(message:Message):Message? {
        val messageClazz = Message::class.java
        val nextField = messageClazz.declaredFields.find {
            it.name == FIELD_NEXT
        }
        return nextField?.get(message) as? Message
    }

    companion object {
        private const val WHAT_CHECK = 0x1234567
        private const val FIELD_M_MESSAGE = "mMessages"
        private const val FIELD_NEXT = "next"

    }
}
