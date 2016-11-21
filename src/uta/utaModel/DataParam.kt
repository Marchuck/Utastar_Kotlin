package uta.utaModel

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 18 : 30
 */
class DataParam {
    enum class OptimizationDirection {
        MIN, MAX
    }

    constructor() {
        mval = 0.0
        moptDir = OptimizationDirection.MAX
    }

    constructor(name: String, `val`: Double, optdir: OptimizationDirection) {
        mname = name
        mval = `val`
        moptDir = optdir
    }

    constructor(`val`: Double, optDir: OptimizationDirection) {
        mval = `val`
        moptDir = optDir
    }

    fun `val`(): Double {
        return mval
    }

    fun setVal(`val`: Double) {
        mval = `val`
    }

    fun name(): String {
        return mname ?: ""
    }

    fun setName(name: String) {
        mname = name
    }

    fun optDir(): OptimizationDirection {
        return moptDir ?: OptimizationDirection.MIN
    }

    fun setOptDir(optDir: OptimizationDirection) {
        moptDir = optDir
    }

    override fun toString(): String {
        return "('" + mname + "':" + java.lang.Double.toString(mval) + ", " + moptDir + ")"
    }

    private var mval: Double = 0.toDouble()
    private var moptDir: OptimizationDirection? = null
    private var mname: String? = null
}