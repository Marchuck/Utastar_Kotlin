package core

import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.GridPane
import tornadofx.*
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
        with (root) {
            for(xd in 1..4) row() {
                piechart ("Imported Fruits") {
                    data("Grapefruit", 12.0)
                    data("Oranges", 25.0)
                    data("Plums", 10.0)
                    data("Pears", 22.0)
                    data("Apples", 30.0)
                }
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
                stackedbarchart("Stock again", CategoryAxis(), NumberAxis()) {
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
        }
    }
}