package uta

import core.UtaApp
import java.util.*
import java.util.logging.Level

/**
 * @author Lukasz
 * @since 21.11.2016.
 */
class UtaSolver {

    private var mvalFuncsOfU: Array<Array<DoubleArray>>? = null
    private var mcritNumb: Int
    private var mdata: MultiTbl
    private var maltNumb: Int
    private var mdeltaValFuncs: Array<Array<DoubleArray>>? = null
    private var msensitivity: Array<DoubleArray>? = null
    private var maltForms: Array<SimplexSolver>? = null
    private var mstdForm: SimplexSolver? = null
    private var maverageWeight: Array<DoubleArray>? = null
    private var mvalFuncsOfW: Array<Array<DoubleArray>>? = null
    private var mmargValFuncs: Array<DoubleArray>? = null
    private var mthreashold: Double
    private var maltScoring: DoubleArray? = null

    constructor(tbl: MultiTbl) {
        mdata = tbl
        mcritNumb = mdata.mcritNumb
        maltNumb = mdata.maltNumb
        mthreashold = 0.05
    }

    fun marginalValFuncs(): Array<DoubleArray> {
        return mmargValFuncs!!
    }

    fun altScoring(): DoubleArray {
        return maltScoring!!
    }

    fun valFuncsOfU() {
        val mult = 0
        val crit = 1
        val valInd = 2

        val emptyCollection = ArrayList<Array<DoubleArray>>()
        for (index in 0..maltNumb - 1) {
            val value = arrayOf(doubleArrayOf(0.0))
            emptyCollection.add(value)
        }
        mvalFuncsOfU = emptyCollection.toTypedArray()
        val tmp = Array(mcritNumb * 2) { DoubleArray(3) }
        for (j in 0..maltNumb - 1) {
            var ind = 0
            for (i in 0..mcritNumb - 1) {
                if (mdata.mgradeTable[i][j] <= mdata.mlimits[i][0][0]) {
                    mdata.mgradeTable[i][j] = mdata.mlimits[i][0][0]
                    tmp[ind][mult] = 1.0
                    tmp[ind][crit] = (i + 1).toDouble()
                    tmp[ind][valInd] = 1.0
                    ind++
                } else if (mdata.mgradeTable[i][j] >= mdata.mlimits[i][mdata.mlimits[i].size - 1][0]) {
                    mdata.mgradeTable[i][j] = mdata.mlimits[i][mdata.mlimits[i].size - 1][0]
                    tmp[ind][mult] = 1.0
                    tmp[ind][crit] = (i + 1).toDouble()
                    tmp[ind][valInd] = mdata.mlimits[i].size.toDouble()
                    ind++
                } else {
                    var k = 1
                    while (k < mdata.mlimits[i].size) {
                        if (mdata.mgradeTable[i][j] == mdata.mlimits[i][k][0]) {
                            tmp[ind][mult] = 1.0
                            tmp[ind][crit] = (i + 1).toDouble()
                            tmp[ind][valInd] = (k + 1).toDouble()
                            ind++
                            k = mdata.mlimits[i].size + 1
                        } else if (mdata.mgradeTable[i][j] < mdata.mlimits[i][k][0]) {
                            val x = (mdata.mgradeTable[i][j] - mdata.mlimits[i][k - 1][0]) / (mdata.mlimits[i][k][0] - mdata.mlimits[i][k - 1][0])

                            tmp[ind][mult] = 1 - x
                            tmp[ind][crit] = (i + 1).toDouble()
                            tmp[ind][valInd] = k.toDouble()
                            ind++

                            tmp[ind][mult] = x
                            tmp[ind][crit] = (i + 1).toDouble()
                            tmp[ind][valInd] = (k + 1).toDouble()
                            ind++

                            k = mdata.mlimits[i].size + 1
                        }
                        k++
                    }
                }
            }
            mvalFuncsOfU!![j] = Array(ind) { DoubleArray(3) }
            ind = 0
            while (ind < mvalFuncsOfU!![j].size) {
                mvalFuncsOfU!![j][ind][mult] = tmp[ind][mult]
                mvalFuncsOfU!![j][ind][crit] = tmp[ind][crit]
                mvalFuncsOfU!![j][ind][valInd] = tmp[ind][valInd]
                ind++
            }
        }

        var dbgStr = "\n numberOfAlternatives: " + maltNumb.toString() + "\n numberOfCriteria: " + mcritNumb
        for (i in 0..maltNumb - 1) {
            dbgStr += "\n\n value function of U ($i):"
            for (k in 0..mvalFuncsOfU!![i].size - 1) {
                dbgStr += "\n[" + k + "] " + mvalFuncsOfU!![i][k][0] + ", " + mvalFuncsOfU!![i][k][1] + ", " + mvalFuncsOfU!![i][k][2]
            }
        }
        UtaApp.mlog.log(Level.INFO, dbgStr)
    }

    fun valFuncsOfW() {
        var arr = ArrayList<Array<DoubleArray>>()
        for (arr_id in 0..maltNumb - 1) {
            val arr_nested = ArrayList<DoubleArray>()

            for (arr_nested_id in 0..mcritNumb - 1) {
                arr_nested.add(doubleArrayOf(0.0))
            }

            var value = arr_nested.toTypedArray()
            arr.add(value)
        }

//        mvalFuncsOfW = Array<Array<DoubleArray>>(maltNumb) { arrayOfNulls<DoubleArray>(mcritNumb) }
        mvalFuncsOfW = arr.toTypedArray()

        for (j in 0..maltNumb - 1) {
            for (i in 0..mcritNumb - 1) {
                mvalFuncsOfW!![j][i] = DoubleArray(mdata.mlimits[i].size - 1)
                for (k in 0..mvalFuncsOfW!![j][i].size - 1) {
                    mvalFuncsOfW!![j][i][k] = 0.0
                }
            }
        }

        for (j in 0..maltNumb - 1) {
            for (ind in 0..mvalFuncsOfU!![j].size - 1) {
                val mult = mvalFuncsOfU!![j][ind][0]
                val crit = mvalFuncsOfU!![j][ind][1].toInt()
                val valInd = mvalFuncsOfU!![j][ind][2].toInt()
                for (k in 0..valInd - 1 - 1) {
                    mvalFuncsOfW!![j][crit - 1][k] = mvalFuncsOfW!![j][crit - 1][k] + mult
                }
            }
        }

        var dbgStr = ""
        for (j in 0..maltNumb - 1) {
            dbgStr += "\n\n value function of W ($j):"
            for (i in 0..mcritNumb - 1) {
                dbgStr += "\n[$i]"
                for (k in 0..mvalFuncsOfW!![j][i].size - 1) {
                    dbgStr += mvalFuncsOfW!![j][i][k].toString() + ", "
                }
                // get rid of the last ','
                dbgStr = dbgStr.substring(0, dbgStr.length - 2)
            }
        }
        UtaApp.mlog.log(Level.INFO, dbgStr)
    }

    fun deltaValFuncs() {

        val emptyList = ArrayList<Array<DoubleArray>>()
        for (j in 0..maltNumb - 1) {
            val row = ArrayList<DoubleArray>()
            for (k in 0..mcritNumb - 1) row.add(doubleArrayOf(0.0))
            emptyList.add(row.toTypedArray())
        }
        mdeltaValFuncs = emptyList.toTypedArray()

        for (j in 0..maltNumb - 1 - 1) {
            for (i in 0..mcritNumb - 1) {
                mdeltaValFuncs!![j][i] = DoubleArray(mdata.mlimits[i].size - 1)
                for (k in 0..mdeltaValFuncs!![j][i].size - 1) {
                    mdeltaValFuncs!![j][i][k] = 0.0
                }
            }
        }

        for (j in 0..maltNumb - 1 - 1) {
            for (i in 0..mcritNumb - 1) {
                for (k in 0..mdeltaValFuncs!![j][i].size - 1) {
                    mdeltaValFuncs!![j][i][k] = mvalFuncsOfW!![j][i][k] - mvalFuncsOfW!![j + 1][i][k]
                }
            }
        }


        var dbgStr = ""
        for (j in 0..maltNumb - 1 - 1) {
            dbgStr += "\n\n deltaFunctions ($j):"
            for (i in 0..mcritNumb - 1) {
                dbgStr += "\n[$i]"
                for (k in 0..mdeltaValFuncs!![j][i].size - 1) {
                    dbgStr += mdeltaValFuncs!![j][i][k].toString() + ", "
                }
                // get rid of the last ','
                dbgStr = dbgStr.substring(0, dbgStr.length - 2)
            }
        }
        UtaApp.mlog.log(Level.INFO, dbgStr)
    }

    fun simplexTbl() {
        var numbOfW = 0
        var numbGrEq = 0
        var grEqInd = 0

        val ofm: DoubleArray
        val aa: Array<DoubleArray>
        val b: IntArray
        val bVals: DoubleArray

        val bSize = 1 + mdeltaValFuncs!!.size

        var varsNumb = 0
        for (i in 0..mcritNumb - 1) {
            for (k in 0..mdeltaValFuncs!![0][i].size - 1) {
                varsNumb++
                numbOfW++
            }
        }
        varsNumb += maltNumb * 2

        for (j in 0..maltNumb - 1 - 1) {
            if (mdata.maltPreference[j] != mdata.maltPreference[j + 1]) {
                varsNumb++
                numbGrEq++
            }
        }
        varsNumb = varsNumb + maltNumb

        ofm = DoubleArray(varsNumb)
        for (j in 0..varsNumb - 1) {
            ofm[j] = 0.0
        }

        aa = Array(bSize) { DoubleArray(varsNumb) }
        for (i in 0..bSize - 1) {
            for (j in 0..varsNumb - 1) {
                aa[i][j] = 0.0
            }
        }

        b = IntArray(bSize)
        for (i in 0..bSize - 1) {
            b[i] = 0
        }

        bVals = DoubleArray(bSize)
        for (i in 0..bSize - 1) {
            bVals[i] = 0.0
        }


        for (j in numbOfW..numbOfW + maltNumb * 2 - 1) {
            ofm[j] = -1.0
        }

        for (j in numbOfW + maltNumb * 2 + numbGrEq..varsNumb - 1) {
            ofm[j] = -2100000000.0
        }
        for (i in 0..bSize - 1 - 1) {
            var j = 0
            for (l in 0..mcritNumb - 1) {
                for (k in 0..mdeltaValFuncs!![0][l].size - 1) {
                    aa[i][j] = mdeltaValFuncs!![i][l][k]
                    j++
                }
            }
            aa[i][numbOfW + i * 2] = -1.0
            aa[i][numbOfW + i * 2 + 1] = 1.0
            aa[i][numbOfW + i * 2 + 2] = 1.0
            aa[i][numbOfW + i * 2 + 3] = -1.0
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                aa[i][numbOfW + maltNumb * 2 + grEqInd] = -1.0
                grEqInd++
            }
            aa[i][numbOfW + maltNumb * 2 + numbGrEq + i] = 1.0
        }

        for (j in 0..numbOfW - 1) {
            aa[bSize - 1][j] = 1.0
        }
        aa[bSize - 1][varsNumb - 1] = 1.0

        for (i in 0..bSize - 1) {
            b[i] = numbOfW + maltNumb * 2 + numbGrEq + i
        }

        for (i in 0..bSize - 1 - 1) {
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                bVals[i] = mthreashold
            }
        }
        bVals[bSize - 1] = 1.0

        mstdForm = SimplexSolver(ofm, aa, b, bVals)

    }


    fun complementarySimplexTbl(ind: Int, el: Double) {
        var numbOfW = 0
        var numbGrEq = 0
        var grEqInd = 0

        val ofm: DoubleArray
        val aa: Array<DoubleArray>
        val b: IntArray
        val bVals: DoubleArray

        val bSize = mdeltaValFuncs!!.size + 1 + 1
        var numbOfVars = 0
        for (i in 0..mcritNumb - 1) {
            for (k in 0..mdeltaValFuncs!![0][i].size - 1) {
                numbOfVars++
                numbOfW++
            }
        }
        numbOfVars = numbOfVars + maltNumb * 2

        for (j in 0..maltNumb - 1 - 1) {
            if (mdata.maltPreference[j] != mdata.maltPreference[j + 1]) {
                numbOfVars++
                numbGrEq++
            }
        }
        numbOfVars = numbOfVars + maltNumb + 1

        ofm = DoubleArray(numbOfVars)
        for (j in 0..numbOfVars - 1) {
            ofm[j] = 0.0
        }

        aa = Array(bSize) { DoubleArray(numbOfVars) }
        for (i in 0..bSize - 1) {
            for (j in 0..numbOfVars - 1) {
                aa[i][j] = 0.0
            }
        }

        b = IntArray(bSize)
        for (i in 0..bSize - 1) {
            b[i] = 0
        }

        bVals = DoubleArray(bSize)
        for (i in 0..bSize - 1) {
            bVals[i] = 0.0
        }

        val strt = (0..ind - 1).sumBy { mdeltaValFuncs!![0][it].size }

        val end = strt + mdeltaValFuncs!![0][ind].size
        for (j in strt..end - 1) {
            ofm[j] = 1.0
        }

        for (j in numbOfW + maltNumb * 2 + numbGrEq..numbOfVars - 1) {
            ofm[j] = -2100000000.0
        }
        for (i in 0..bSize - 1 - 1 - 1) {
            var j = 0
            for (l in 0..mcritNumb - 1) {
                for (k in 0..mdeltaValFuncs!![0][l].size - 1) {
                    aa[i][j] = mdeltaValFuncs!![i][l][k]
                    j++
                }
            }
            aa[i][numbOfW + i * 2] = -1.0
            aa[i][numbOfW + i * 2 + 1] = 1.0
            aa[i][numbOfW + i * 2 + 2] = 1.0
            aa[i][numbOfW + i * 2 + 3] = -1.0
            if (mdata.maltPreference[i] != mdata.maltPreference[i + 1]) {
                aa[i][numbOfW + maltNumb * 2 + grEqInd] = -1.0
                grEqInd++
            }
            aa[i][numbOfW + maltNumb * 2 + numbGrEq + i] = 1.0
        }

        for (j in 0..numbOfW - 1) {
            aa[bSize - 1 - 1][j] = 1.0
        }
        aa[bSize - 1 - 1][numbOfVars - 1 - 1] = 1.0

        for (j in numbOfW..numbOfW + maltNumb * 2 - 1) {
            aa[bSize - 1][j] = 1.0
        }
        aa[bSize - 1][numbOfVars - 1] = 1.0

        for (i in 0..bSize - 1) {
            b[i] = numbOfW + maltNumb * 2 + numbGrEq + i
        }

        (0..bSize - 1 - 1 - 1)
                .filter { mdata.maltPreference[it] != mdata.maltPreference[it + 1] }
                .forEach { bVals[it] = mthreashold }
        bVals[bSize - 1 - 1] = 1.0
        bVals[bSize - 1] = mstdForm!!.profit() + el

        maltForms!![ind] = SimplexSolver(ofm, aa, b, bVals)

    }

    fun emptyArrayOfDoubleArrays(capacity: Int): Array<DoubleArray> {
        val row = ArrayList<DoubleArray>()
        for (k in 0..capacity - 1) row.add(doubleArrayOf(0.0))
        return row.toTypedArray()
    }

    fun UtaSolve(e: Double): Array<DoubleArray> {
        valFuncsOfU()
        valFuncsOfW()
        deltaValFuncs()
        simplexTbl()

        val typedEmptyArray = emptyArrayOfDoubleArrays(mcritNumb)

        maverageWeight = typedEmptyArray
        mmargValFuncs = typedEmptyArray

        maltForms = arrayOfSimplexes(mcritNumb)
        msensitivity = typedEmptyArray

        for (i in 0..mcritNumb - 1) {
            complementarySimplexTbl(i, e)
            msensitivity!![i] = maltForms!![i].findSolution()
        }

        val tmp = DoubleArray(msensitivity!![0].size)
        for (i in 0..msensitivity!![0].size - 1) {
            tmp[i] = 0.0
            for (j in msensitivity!!.indices) {
                tmp[i] = tmp[i] + msensitivity!![j][i]
            }
            tmp[i] = tmp[i] / 3
        }

        for (i in 0..mcritNumb - 1) {
            maverageWeight!![i] = DoubleArray(mdeltaValFuncs!![0][i].size)
        }

        var k = 0
        for (i in 0..mcritNumb - 1) {
            for (j in 0..mdeltaValFuncs!![0][i].size - 1) {
                maverageWeight!![i][j] = tmp[k]
                k += 1
            }
        }

        for (i in 0..mcritNumb - 1) {
            mmargValFuncs!![i] = DoubleArray(mdeltaValFuncs!![0][i].size + 1)
        }

        for (i in 0..mcritNumb - 1) {
            tmp[i] = 0.0
            var j: Int
            j = 0
            while (j < mdeltaValFuncs!![0][i].size) {
                mmargValFuncs!![i][j] = tmp[i]
                tmp[i] = tmp[i] + maverageWeight!![i][j]
                j++
            }
            mmargValFuncs!![i][j] = tmp[i]
        }

        maltScoring = DoubleArray(maltNumb)

        for (i in 0..maltNumb - 1) {
            maltScoring!![i] = 0.0
            for (j in 0..mcritNumb - 1) {
                k = 0
                while (k < mdeltaValFuncs!![0][j].size) {
                    maltScoring!![i] = maltScoring!![i] + maverageWeight!![j][k] * mvalFuncsOfW!![i][j][k]
                    k++
                }
            }
        }

        return maverageWeight!!
    }

    private fun arrayOfSimplexes(capacity: Int): Array<SimplexSolver>? {
        val arr = ArrayList<SimplexSolver>()
        for (j in 0..capacity - 1) {
            arr.add(SimplexSolver())
        }
        return arr.toTypedArray()
    }

}