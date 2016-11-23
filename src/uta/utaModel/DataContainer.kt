package uta.utaModel

import core.UtaException
import java.io.BufferedReader
import java.io.IOException
import java.util.*

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 18 : 29
 */
class DataContainer {
    constructor() {
        mdata = LinkedList<DataModel>()
    }

    constructor(data: LinkedList<DataModel>) {
        mdata = data
    }
    constructor(buffer: BufferedReader) {
        importFromFile(buffer)
    }

    fun data(): LinkedList<DataModel> {
        return mdata
    }

    fun rows(): Int {
        return mdata.size
    }

    fun columns(): Int {
        if (mdata.size == 0) {
            return 0
        }
        return mdata.first.size()
    }

    @Throws(IOException::class, UtaException::class)
    fun importFromFile(dataFile: BufferedReader) {
        // Lines which start with '#' are ignored
        // Line which starts with '$' contains labels for parameters
        // Line which starts with '@' contains information, whether value should be maximized or minimized
        // Values are separated by '|', whitespaces are ignored unless they are part of a label

        var line: String? = dataFile.readLine()
        val labels = LinkedList<String>()
        val directions = LinkedList<DataParam.OptimizationDirection>()
        while (line != null) {
            // ignore empty lines
            if (line.length >= 1) {
                var pos = 0
                while (pos < line.length) {
                    var c = line[pos]
                    if (Character.isWhitespace(c)) {
                        // ignore whitespace
                        ++pos
                        continue
                    }

                    if (c == '#') {
                        // skip comment, get new line
                        break
                    }
                    if (c == '@') {
                        var strDir = ""
                        ++pos
                        while (pos < line.length) {
                            c = line[pos]
                            if (c == '|') {
                                directions.add(DataParam.OptimizationDirection.valueOf(strDir))
                                strDir = ""
                            } else if (Character.isWhitespace(c)) {
                                // skip whitespace
                                ++pos
                                continue
                            } else {
                                strDir += c
                            }
                            ++pos
                        }
                        if (strDir.length > 0) {
                            directions.add(DataParam.OptimizationDirection.valueOf(strDir))
                        }
                        // get new line
                        break
                    }

                    if (c == '$') {
                        // read labels
                        var label = ""
                        ++pos
                        while (pos < line.length) {
                            c = line[pos]
                            if (c == '|') {
                                labels.add(label)
                                label = ""
                            } else {
                                label += c
                            }
                            ++pos
                        }
                        if (label.length > 0) {
                            labels.add(label)
                        }
                        // get new line
                        break
                    }

                    if (Character.isDigit(c)) {
                        if (labels.size == 0 || directions.size == 0) {
                            val strLabSize = labels.size.toString()
                            val strDirSize = directions.size.toString()
                            throw UtaException("Labels ($strLabSize) and directions ($strDirSize) of parameters need to be defined before values")
                        }
                        // this is a row with the data
                        val dataMod = DataModel()
                        dataMod.importData(labels, directions, line)
                        mdata.add(dataMod)
                        // get new line
                        break
                    }
                    ++pos
                }
            }

            line = dataFile.readLine()
        }

        if (mdata.size <= 0) {
            throw UtaException("Data file is empty")
        }
        if (mdata.first.params().size <= 0) {
            throw UtaException("No parameters?")
        }
    }

    override fun toString(): String {
        var str = ""
        var row = 1
        for (dataMod in mdata) {
            str += "\t" + Integer.toString(row++) + ") " + dataMod + "\n"
        }
        return str
    }

    private var mdata: LinkedList<DataModel> = LinkedList<DataModel>()
}