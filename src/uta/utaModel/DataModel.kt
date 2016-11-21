package uta.utaModel

import java.util.*

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 18 : 30
 */
class DataModel {

    constructor() {
        mparams = LinkedList<DataParam>()
    }

    /**
     * Parse string with values of data and associate those values
     * to names provided in the first param.

     * @param names Names of params
     * *
     * @param directions Direction of optimization of a value
     * *
     * @param strVal String which contains values of params
     */
    fun importData(names: LinkedList<String>, directions: LinkedList<DataParam.OptimizationDirection>, strVal: String) {
        var strNumb = ""
        var paramNumb = 0
        for (pos in 0..strVal.length - 1) {
            val c = strVal[pos]

            if (Character.isDigit(c) || c == '.') {
                strNumb += c
                continue
            }

            if ((Character.isWhitespace(c) || c == '|') && strNumb.length > 0) {
                add(names[paramNumb], strNumb, directions[paramNumb++])
                strNumb = ""
            }
        }

        if (strNumb.length > 0) {
            add(names[paramNumb], strNumb, directions[paramNumb++])
        }
    }

    fun add(label: String, strNumb: String, direction: DataParam.OptimizationDirection) {
        val numb = java.lang.Double.valueOf(strNumb)
        mparams.add(DataParam(label, numb, direction))
    }

    fun params(): LinkedList<DataParam> {
        return mparams
    }

    fun size(): Int {
        return mparams.size
    }

    override fun toString(): String {
        var str = ""
        for (param in mparams) {
            str += param.toString() + ", "
        }

        // get rid of the last ','
        return str.substring(0, str.length - 2)
    }

    private var mparams: LinkedList<DataParam>
}