package lei.cheng.performancetools.mainlock

import lei.cheng.performancetools.mainlock.handler.ThreadLockMeta

interface ILockResult {

    fun handleResult(results: List<ThreadLockMeta>)
}