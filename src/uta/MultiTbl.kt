package uta

import java.util.*

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 18 : 41
 */
class MultiTbl(limits: Array<Array<DoubleArray>>, crit: Array<DoubleArray>, pref: IntArray) {

    var mlimits: Array<Array<DoubleArray>>
    var mcritScale: BooleanArray
    var mcritNumb: Int = 0
    var maltNumb: Int = 0
    var mgradeTable: Array<DoubleArray>
    var mcritWeight: DoubleArray
    var maltPreference: IntArray

    init {
        mcritNumb = crit.size
        maltNumb = crit[0].size
        mcritWeight = DoubleArray(mcritNumb)
        mcritScale = BooleanArray(mcritNumb)

        val list = ArrayList<DoubleArray>()
        for (jot in 0..mcritNumb) list.add(Array<DoubleArray>())
        mlimits = list.toTypedArray()
        for (i in 0..mcritNumb - 1) {
            mlimits[i] = Array(limits[i].size) { DoubleArray(2) }
        }
        mgradeTable = Array(mcritNumb) { DoubleArray(maltNumb) }
        maltPreference = IntArray(maltNumb)
        for (i in 0..mcritNumb - 1) {
            mcritWeight[i] = (1 / mcritNumb).toDouble()
            mcritScale[i] = false
            for (k in 0..mlimits[i].size - 1) {
                mlimits[i][k][0] = limits[i][k][0]
                mlimits[i][k][1] = limits[i][k][1]
            }
            System.arraycopy(crit[i], 0, mgradeTable[i], 0, maltNumb)
        }
        System.arraycopy(pref, 0, maltPreference, 0, maltNumb)
    }

}