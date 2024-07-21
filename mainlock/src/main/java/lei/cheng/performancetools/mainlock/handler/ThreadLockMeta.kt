package lei.cheng.performancetools.mainlock.handler

data class ThreadLockMeta(
    val thread:Thread
) {
    val traces : String
        get() = thread.dumpStack()

    val name = thread.name
}


fun Thread.dumpStack() : String {
    val sb = StringBuilder()
    this.stackTrace.forEachIndexed { index, trace->
        val className = trace.className
        val methodName = trace.methodName
        val fieldName = trace.fileName
        val lineNumber = trace.lineNumber
        for (i in 0..index) {
            sb.append(" ")
        }
        sb.append("${className}#${methodName}${fieldName} in line ${lineNumber}")
        sb.append("\n")
    }
    return sb.toString()
}