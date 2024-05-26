package lei.cheng.performancetools.init

/**
 * @author chenglei01
 * @date 2024/5/2
 * @time 00:27
 */
class Config private constructor(builder: Builder) {
    private var memoryConfig: MemoryConfig? = null

    init {
        memoryConfig = builder.memoryConfig
    }

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).builder()
    }

    class Builder {
        var memoryConfig: MemoryConfig? = null
        fun builder() = Config(this)
    }
}

class MemoryConfig private constructor(
    builder: Builder
) {
    private var enable: Boolean = false
    private var packageNameMatcher: IPackageMatcher? = null

    init {
        this.enable = builder.enable
        this.packageNameMatcher = builder.packageNameMatcher
    }

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block)
            .builder()

    }

    class Builder {
        var enable: Boolean = false
        var packageNameMatcher: IPackageMatcher? = null

        fun builder() = MemoryConfig(this)
    }
}

interface IPackageMatcher {
    fun match(pkg: String): Boolean
}