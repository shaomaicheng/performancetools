package lei.cheng.performancetools.block

/**
 * @author chenglei01
 * @date 2024/6/8
 * @time 22:17
 */
fun printStack(traces:Array<StackTraceElement>) :String{
    val sb = StringBuilder()
    traces.forEachIndexed { index, stackTraceElement ->
        sb.append("\tat $stackTraceElement")
        sb.append("\n")
    }
    return sb.toString()
}