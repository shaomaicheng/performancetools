package lei.cheng.performancetools.block

import lei.cheng.performancetools.block.debug.BlockTraceEntity

/**
 * @author chenglei01
 * @date 2024/6/10
 * @time 13:51
 */
interface IBlockDB {
    fun saveBlockTrace(runningTime:Long, traces: ArrayList<Array<StackTraceElement>>)
    fun queryTime():List<Long>
    fun queryTraces(time:Long):List<BlockTraceEntity>
}