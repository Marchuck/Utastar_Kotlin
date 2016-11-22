package uta

import core.UtaApp
import uta.utaModel.DataContainer
import uta.utaModel.DataParam
import java.util.logging.Level

/**
 * @author Lukasz
 * @since 22.11.2016.
 */
object Utastar {

    /**
     * Find most optimal solutions among data.

     * @param data
     * *
     * @return Couple of most optimal results
     */
    fun optimize(data: DataContainer): DoubleArray {
        val dt = data.data()
        //temporaly creating table for algorithm
        val criteria = arrayOf(arrayOf(doubleArrayOf(100.0, 0.0), doubleArrayOf(2500.0, 0.0), doubleArrayOf(5000.0, 0.0)), arrayOf(doubleArrayOf(0.1, 0.0), doubleArrayOf(0.55, 0.0), doubleArrayOf(1.0, 0.0)), arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(5.0, 0.0), doubleArrayOf(10.0, 0.0)), arrayOf(doubleArrayOf(0.1, 0.0), doubleArrayOf(0.55, 0.0), doubleArrayOf(1.0, 0.0)), arrayOf(doubleArrayOf(-10.0, 0.0), doubleArrayOf(-5.0, 0.0), doubleArrayOf(-1.0, 0.0)), arrayOf(doubleArrayOf(0.1, 0.0), doubleArrayOf(0.55, 0.0), doubleArrayOf(1.0, 0.0)))
        val rows = data.rows()
        val cols = data.columns()
        val alternatives = Array(cols - 1) { DoubleArray(rows) }
        // DM preference order
        val p = IntArray(rows)

        for (i in 0..rows - 1) {
            for (j in 0..cols - 1 - 1) {
                val prm = dt[i].params()[j]
                alternatives[j][i] = prm.`val`()
                if (prm.optDir() == DataParam.OptimizationDirection.MIN) {
                    alternatives[j][i] = -alternatives[j][i]
                }
            }
            p[i] = dt[i].params().last.`val`() as Int
        }

        // run utastar calculations
        val MultiTable = MultiTbl(criteria, alternatives, p)
        val ProjectX = UtaSolver(MultiTable)
        var uted = ProjectX.UtaSolve(0.00001)

        // BEGIN debug info
        var i: Int
        var dbgStr = "\n averageWeightMatrix :[\n"
        i = 0
        while (i < uted.size) {
            for (t in 0..uted[i].size - 1) {
                dbgStr += " " + uted[i][t]
            }
            dbgStr += "\n"
            i++
        }
        dbgStr += "]"

        uted = ProjectX.marginalValFuncs()
        dbgStr += "\n\n marginalValueFunctions: [\n"
        i = 0
        while (i < uted.size) {
            for (t in 0..uted[i].size - 1) {
                dbgStr += " " + uted[i][t]
            }
            dbgStr += "\n"
            i++
        }
        dbgStr += "]"

        val utela = ProjectX.altScoring()
        dbgStr += "\n\n alternativeScoring: ["
        for (t in utela.indices) {
            dbgStr += " " + utela[t]
        }
        dbgStr += "]"

        UtaApp.mlog.log(Level.INFO, dbgStr)
        // END debug info

        return utela
    }
}
