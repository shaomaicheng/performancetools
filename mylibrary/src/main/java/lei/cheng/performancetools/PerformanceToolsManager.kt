package lei.cheng.performancetools;

import android.content.Context
import android.os.Build
import android.os.Debug
import androidx.annotation.RequiresApi
import lei.cheng.performancetools.init.copyApk
import java.io.File

/**
 * @author chenglei01
 * @date 2023/9/6
 * @time 21:47
 */
class PerformanceToolsManager {
    init {

    }

    fun init(context: Context){
        // apm

        System.loadLibrary(PERFORMANCETOOLS_LIB_NAME)
        // 日志根目录传给jni
        val logDir = File(context.externalCacheDir, PERFORMANCETOOLS_LOG_DIR)
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        val logDirPath = logDir.absolutePath
        JavaConfig.logDirPath = logDirPath

        // 读取现有的看看内容
        /*File(logDirPath).listFiles()?.forEach { file->
            file?.let { file->
                val random = RandomAccessFile(file,"rw")
                val fileChannel = random.channel
                val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
                var i = 0
                var content = ""
                while (i < mappedByteBuffer.limit())
                {
                    val char = mappedByteBuffer.get()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        content+=(String(byteArrayOf(char),StandardCharsets.UTF_8))
                    }

                    i++
                }
                fileChannel.close()
                random.close()
                Log.e("chenglei_java", "日志文件内容：$content")
            }
        }
*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {

                enableInner(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enableInner(context: Context){
        var so = PERFORMANCETOOLS_LIB_NAME
        System.loadLibrary(so)
        val path = copyApk(context, so)
        path?.let { path->
            attachJvmtiAgent(path, context)
        }

    }

    private fun attachJvmtiAgent(path:File, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Debug.attachJvmtiAgent(path.absolutePath, null, context.classLoader)
        } else {
            val debugClazz = Class.forName("dalvik.system.VMDebug")
            val attachAgentMethod = debugClazz.getMethod("attachAgent",String::class.java)
            attachAgentMethod.isAccessible = true
            attachAgentMethod.invoke(null, path)
        }
    }

    companion object {
        private const val PERFORMANCETOOLS_LIB_NAME = "performancetools"
        private const val PERFORMANCETOOLS_LOG_DIR = "performancetools_log"
    }
}