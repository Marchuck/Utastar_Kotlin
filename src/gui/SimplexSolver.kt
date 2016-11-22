package gui

/**
 * @author Lukasz
 * @since 22.11.2016.
 */

class SimplexSolver(ofm: DoubleArray, a: Array<DoubleArray>, b: IntArray, bVals: DoubleArray) {

//    constructor() : this(DoubleArray(0), emptyArray(), IntArray(0), DoubleArray(0))

    var mbaseSize: Int = 0
    var msolution: DoubleArray = DoubleArray(6)
    var mvarsNumb: Int = 0
    var mprofit: Double = 0.toDouble()
    var mstepNumb: Int = 0
    var mbaseVector: IntArray
    var malphaTbl: Array<DoubleArray>
    var mmultipliers: DoubleArray
    var mlimitProfits: DoubleArray
    var mbaseVals: DoubleArray
    var mlineInd: Int = 0
    var mcolumnInd: Int = 0
    var mblocked: IntArray

    init {
        mbaseSize = a.size
        mvarsNumb = a[0].size

        mmultipliers = DoubleArray(mvarsNumb)
        mblocked = IntArray(mvarsNumb)
        mlimitProfits = DoubleArray(mvarsNumb)
        malphaTbl = Array(mbaseSize) { DoubleArray(mvarsNumb) }
        mbaseVector = IntArray(mbaseSize)
        mbaseVals = DoubleArray(mbaseSize)

        for (i in 0..mbaseSize - 1) {
            mbaseVector[i] = b[i]
            mbaseVals[i] = bVals[i]
            System.arraycopy(a[i], 0, malphaTbl[i], 0, mvarsNumb)
        }

        for (j in 0..mvarsNumb - 1) {
            mmultipliers[j] = ofm[j]
            mblocked[j] = 0
        }

        mstepNumb = 0
        mlineInd = 0
        mcolumnInd = 0
        mprofit = 0.0
    }

    fun isVarBlocked(ind: Int): Int {
        if (ind < mvarsNumb && ind >= 0) {
            return mblocked[ind]
        } else {
            return 1
        }
    }

    fun profit(): Double {
        return mprofit
    }

    fun solution(): DoubleArray {
        msolution = DoubleArray(mvarsNumb)

        for (j in 0..mvarsNumb - 1) {
            msolution[j] = 0.0
        }

        for (i in 0..mbaseSize - 1) {
            val baseVarInd = mbaseVector[i]
            msolution[baseVarInd] = mbaseVals[i]
        }
        return msolution
    }

    fun calcLimitProfits(): DoubleArray {
        for (j in 0..mvarsNumb - 1) {
            var Zj = 0.0
            if (isVarBlocked(j) == 0) {
                for (i in 0..mbaseSize - 1) {
                    val `var` = mbaseVector[i]
                    Zj = Zj + malphaTbl[i][j] * mmultipliers[`var`]
                }
                mlimitProfits[j] = mmultipliers[j] - Zj
            }
        }
        return mlimitProfits
    }

    val isOptimal: Boolean
        get() {
            var optimal = true

            var j = 0
            while (j < mvarsNumb) {
                if (isVarBlocked(j) == 0) {
                    if (mlimitProfits[j] > 0) {
                        optimal = false
                        j = mvarsNumb + 1
                    }
                }
                j++
            }
            return optimal
        }

    val isFinite: Boolean
        get() {
            var finite = false

            var j = 0
            while (j < mvarsNumb) {
                if (isVarBlocked(j) == 0) {
                    if (mlimitProfits[j] > 0) {
                        var positive = false
                        var i = 0
                        while (i < mbaseSize) {
                            if (malphaTbl[i][j] > 0) {
                                positive = true
                                i = mbaseSize + 1
                            }
                            i++
                        }
                        if (positive == false) {
                            finite = true
                            j = mvarsNumb + 1
                        }
                    }
                }
                j++
            }
            return finite
        }

    fun findVarIn() {
        var tmp = 0.0
        var tmpInd = -1

        for (j in 0..mvarsNumb - 1) {
            if (isVarBlocked(j) == 0 && mlimitProfits[j] > 0 && mlimitProfits[j] > tmp) {
                tmp = mlimitProfits[j]
                tmpInd = j
            }
        }
        mcolumnInd = tmpInd
    }

    fun findVarOut() {
        var tmp = 0.0
        var tmpInd = -1

        run {
            var i = 0
            while (i < mbaseSize) {
                if (malphaTbl[i][mcolumnInd] > 0) {
                    tmp = mbaseVals[i] / malphaTbl[i][mcolumnInd]
                    tmpInd = i
                    i = mbaseSize + 1
                }
                i++
            }
        }
        for (i in tmpInd + 1..mbaseSize - 1) {
            if (malphaTbl[i][mcolumnInd] > 0) {
                if (mbaseVals[i] / malphaTbl[i][mcolumnInd] < tmp) {
                    tmp = mbaseVals[i] / malphaTbl[i][mcolumnInd]
                    tmpInd = i
                }
            }
        }
        mlineInd = tmpInd
    }

    fun nextSimplexTbl() {
        val guideEl = malphaTbl[mlineInd][mcolumnInd]

        mbaseVector[mlineInd] = mcolumnInd

        mbaseVals[mlineInd] = mbaseVals[mlineInd] / guideEl
        for (j in 0..mvarsNumb - 1) {
            malphaTbl[mlineInd][j] = malphaTbl[mlineInd][j] / guideEl
        }

        for (i in 0..mbaseSize - 1) {
            if (i != mlineInd) {
                val lineHdr = malphaTbl[i][mcolumnInd]
                mbaseVals[i] = mbaseVals[i] - lineHdr * mbaseVals[mlineInd]
                for (j in 0..mvarsNumb - 1) {
                    malphaTbl[i][j] = malphaTbl[i][j] - lineHdr * malphaTbl[mlineInd][j]
                }
            }
        }
    }

    fun calcProfits() {
        mprofit = mbaseVector.indices.sumByDouble { mbaseVals[it] * mmultipliers[mbaseVector[it]] }
    }

    fun findSolution(): DoubleArray {
        calcLimitProfits()
        while (true) {
            if (isOptimal || isFinite) {
                break
            } else {
                findVarIn()
                findVarOut()
                mstepNumb++
                nextSimplexTbl()
                calcProfits()
                calcLimitProfits()
            }
        }
        return solution()
    }

}