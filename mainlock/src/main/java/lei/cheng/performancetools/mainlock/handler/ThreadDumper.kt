package lei.cheng.performancetools.mainlock.handler

class ThreadDumper {

    fun getAllThreads() : List<Thread> {
        val threads = arrayListOf<Thread>()
        val rootGroup = getRootThreadGroup()
        if (rootGroup != null) {
            enumerateThreads(rootGroup, threads)
        }
        return threads
    }

    private fun getRootThreadGroup(): ThreadGroup? {
        var group = Thread.currentThread().threadGroup
        while (group?.parent != null) {
            group = group.parent
        }
        return group
    }

    private fun enumerateThreads(group: ThreadGroup, threads:ArrayList<Thread>) {
        val groupThreads = arrayOfNulls<Thread>(group.activeCount())
        val numThreads = group.enumerate(groupThreads)
        for (i in 0 until numThreads) {
            groupThreads[i]?.let {
                threads.add(it)
            }
        }
        val parentGroup = group.parent
        if (parentGroup != null && parentGroup != group) {
            enumerateThreads(parentGroup, threads)
        }
    }
}