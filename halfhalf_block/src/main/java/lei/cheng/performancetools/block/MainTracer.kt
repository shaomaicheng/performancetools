package lei.cheng.performancetools.block

import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @author halflinecode
 * @date 2024/6/8
 * @time 22:25
 * 主线程队列监控
 */
object MainTracer {
    @Volatile
    private var running = false
    private var runningTime = 0L
    private var sendTime = 0L

    private var job: Job? = null

    private var config: BlockConfig? = null

    private var mainThreadTraces = ConcurrentHashMap<Long, ArrayList<Array<StackTraceElement>>>()

    fun start(config: BlockConfig, sendTime:Long) {
        this.config = config
        this.sendTime = sendTime
        if (running) {
            return
        }
        running = true
        runningTime = SystemClock.elapsedRealtime()
        job = ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                if (SystemClock.elapsedRealtime() - runningTime >= 5 * 1000L) {
                    stop()
                    break
                }
                val mainThread = Looper.getMainLooper().thread
                val traces = mainThread.stackTrace
                if (!mainThreadTraces.containsKey(sendTime)) {
                    mainThreadTraces[sendTime] = ArrayList()
                }
                mainThreadTraces[sendTime]?.add(traces)
                delay(config.traceInterval)
            }
        }
    }

    fun stop() {
        if (!running) {
            return
        }
        val stacks = mainThreadTraces.remove(sendTime)
        running = false
        val tempRunningTime = runningTime
        runningTime = 0L
        sendTime = 0L
        job?.cancel()
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            if (config == null) return@launch
            //存本地
            val subDir = "blockStacks"
            val fileName = "blockStack_${System.currentTimeMillis()}_${SystemClock.elapsedRealtime()}";
            val dir = if (config?.dir?.isNotEmpty()==true) {
                File(config!!.dir, subDir)
            } else {
                File(config!!.application.externalCacheDir,subDir)
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir,fileName)
            if (file.createNewFile()) {
                kotlin.runCatching {
                    val write = file.sink()
                    val bufferSink = write.buffer()
                    stacks?.forEach {
                        val trace = printStack(it)
                        bufferSink.writeUtf8(trace)
                        bufferSink.writeUtf8("\n");
                    }
                    bufferSink.close()
                    write.close()
                    config?.uploader?.upload(file)
                    stacks?.let { stacks->
                        config?.db?.saveBlockTrace(tempRunningTime, stacks)
                    }

                }
            }
            Logger.log("halfline", "收集卡顿堆栈数量:${mainThreadTraces.size}")
        }
    }
}

