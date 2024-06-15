package lei.cheng.performancetools.block.debug

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lei.cheng.performancetools.block.BlockMonitor
import lei.cheng.performancetools.block.Logger

/**
 * @author halflinecode
 * @date 2024/6/16
 * @time 00:04
 */
class TraceByBlockViewModel:ViewModel() {

    val traces = MutableLiveData<List<BlockTraceEntity>>()

    val db by lazy {
        BlockMonitor.config.db
    }

    fun queryTraces(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db?.queryTraces(time)?.let {traces->
                Logger.log("halfline","traces数目：${traces.size}")
                this@TraceByBlockViewModel.traces.postValue(traces)
            }
        }
    }
}