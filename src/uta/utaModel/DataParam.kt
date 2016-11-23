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

    constructor(name: String, _val: Double, optdir: OptimizationDirection) {
        mname = name
        mval = _val
        moptDir = optdir
    }

    constructor(_val: Double, optDir: OptimizationDirection) {
        mval = _val
        moptDir = optDir
    }

    fun _val(): Double {
        return mval
    }

    fun setVal(_val: Double) {
        mval = _val
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