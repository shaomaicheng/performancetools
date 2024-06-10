package lei.cheng.performancetools.block

import androidx.lifecycle.LiveData

/**
 * @author chenglei01
 * @date 2024/6/10
 * @time 13:51
 */
interface IBlockDB {
    fun saveBlockTrace(runningTime:Long, traces: ArrayList<Array<StackTraceElement>>)
    fun queryTIme():List<Long>
}