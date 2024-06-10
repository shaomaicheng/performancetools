package lei.cheng.performancetools.block.debug

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lei.cheng.performancetools.block.IBlockDB
import lei.cheng.performancetools.block.printStack

/**
 * @author chenglei01
 * @date 2024/6/10
 * @time 14:34
 */
class BlockTraceDB(val application: Application):IBlockDB {

    private val db by lazy {
        Room.databaseBuilder(application, BlockDataBase::class.java, "blockTrace")
            .build()
    }

    override fun saveBlockTrace(runningTime: Long, traces: ArrayList<Array<StackTraceElement>>) {
        val params = traces.map {
            val mills = System.currentTimeMillis()
            val recordId = "${runningTime}_${mills}"
            BlockTraceEntity(recordId,runningTime, mills, printStack(it))
        }.toTypedArray()
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            db.blockTraceDao().insertAll(*params)
        }
    }

    override fun queryTIme(): List<Long> {
        return db.blockTraceDao().queryTimes(System.currentTimeMillis() - 24 * 60 * 60 * 1000L)
    }
}