package core

import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.GridPane
import tornadofx.*
import uta.Utastar
import uta.utaModel.DataContainer
import java.io.BufferedReader
import java.io.FileReader


/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 48
 */

class DynamicGraph : View("graph") {
    override val root = GridPane()

    init {
        with(root) {
            row {
                barchart("Stock Monitoring, 2010", CategoryAxis(), NumberAxis()) {
                    series("Portfolio 1") {
                        data("Jan", 23)
                        data("Feb", 14)
                        data("Mar", 15)
                    }
                    series("Portfolio 2") {
                        data("Jan", 11)
                        data("Feb", 19)
                        data("Mar", 27)
                    }
                }

                linechart("linechart", CategoryAxis(), NumberAxis()) {
                    series("month") {
                        data("jan", 10)
                        data("feb", 20)
                        data("mar", 5)
                    }
                    series("week") {
                        data("jan", 1)
                        data("feb", 2)
                    }
                }
            }
            vbox {
                button("open input data") {
                    isDefaultButton = true

                    setOnAction {
//                        val fileChooser = FileChooser()
//                        val file = fileChooser.showOpenDialog()
                        val filePath = "data.txt"
                        val mData = filePath
                                .map { FileReader(filePath) }
                                .map(::BufferedReader)
                                .map(::DataContainer)
                                .map { dataContainer -> refreshGraph(dataContainer) }


                    }

                }

            }
        }
    }

    fun refreshGraph(dataContainer: DataContainer): Unit {
        val scoring = Utastar.optimize(dataContainer)
        val data = dataContainer.data()
        //todo: draw results!
    }
}