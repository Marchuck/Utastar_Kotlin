package core

import gui.Utastar
import javafx.application.Platform
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import tornadofx.*
import uta.utaModel.DataContainer
import java.io.BufferedReader
import java.io.FileReader
import java.util.logging.Level


/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 48
 */

class MainGui : View("Uta Star") {
    override val root = GridPane()


    init {
        Platform.setImplicitExit(false)

        with(root) {

            row {
                button("load data") {
                    isDefaultButton = true

                    setOnAction {
                        val fileChooser = FileChooser();
                        val file = fileChooser.showOpenDialog(primaryStage)

                        file.absolutePath
                                .map { FileReader(file.absolutePath) }
                                .map(::BufferedReader)
                                .map(::DataContainer)
                                .map { dataContainer ->
                                    ControllerApp.mlog.log(Level.FINE, "refresh graph")
                                    val scoring = Utastar.optimize(dataContainer)
                                    //val viewToAdd = DynamicGraph(scoring, dataContainer)
                                    val pane = GridPane()
                                    with(pane){
                                        barchart("Utastar", CategoryAxis(), NumberAxis()) {
                                            series("Utastar alternatives scoring") {
                                                for (j in 0..scoring.size-1)
                                                    data("Property " + j.toString(), scoring[j])
                                            }
                                        }
                                    }



                                    // UtaStarGraphApp(scoring, dataContainer).main(arrayOf(""))
                                }
                    }
                }
            }
            row {

            }

        }
    }
}