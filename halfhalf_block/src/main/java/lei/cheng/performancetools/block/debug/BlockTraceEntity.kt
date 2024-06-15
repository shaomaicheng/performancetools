package lei.cheng.performancetools.block.debug

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author halflinecode
 * @date 2024/6/10
 * @time 14:34
 */
@Entity
data class BlockTraceEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name="runningTime") val runningTime:Long,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "trace") val trace: String
)