package lei.cheng.performancetools.block.debug

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase

/**
 * @author chenglei01
 * @date 2024/6/10
 * @time 14:37
 */
@Dao
interface BlockTraceDao {
    @Insert
    fun insertAll(vararg traces: BlockTraceEntity)

    @Query("select DISTINCT runningTime FROM BlockTraceEntity where time >= :rangeTime")
    fun queryTimes(rangeTime:Long):List<Long>
}


@Database(entities = [BlockTraceEntity::class], version = 1)
abstract class BlockDataBase : RoomDatabase() {
    abstract fun blockTraceDao(): BlockTraceDao
}