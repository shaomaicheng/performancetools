package lei.cheng.performancetools.block.debug

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lei.cheng.performancetools.block.BlockMonitor

/**
 * @author chenglei01
 * @date 2024/6/10
 * @time 17:58
 */
class BlockTraceViewModel:ViewModel() {
    val times: MutableLiveData<List<Long>> = MutableLiveData()

    val db by lazy {
        BlockMonitor.config.db
    }

    fun queryTimes() {
        db?:return
        viewModelScope.launch(Dispatchers.IO) {
            val list = db!!.queryTIme()
            withContext(Dispatchers.Main) {
                times.value = list
            }
        }
    }
}